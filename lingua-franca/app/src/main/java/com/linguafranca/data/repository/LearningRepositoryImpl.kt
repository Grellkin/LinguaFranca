package com.linguafranca.data.repository

import com.linguafranca.data.local.dao.LearningProgressDao
import com.linguafranca.data.local.entity.LearningProgressEntity
import com.linguafranca.domain.model.LearningProgress
import com.linguafranca.domain.repository.LearningRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt

@Singleton
class LearningRepositoryImpl @Inject constructor(
    private val learningProgressDao: LearningProgressDao
) : LearningRepository {

    override fun observeProgressByWordId(wordId: String): Flow<LearningProgress?> {
        return learningProgressDao.observeProgressByWordId(wordId).map { it?.toDomain() }
    }

    override fun observeProgressByDictionary(dictionaryId: String): Flow<List<LearningProgress>> {
        return learningProgressDao.observeProgressByDictionary(dictionaryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProgressByWordId(wordId: String): LearningProgress? {
        return learningProgressDao.getProgressByWordId(wordId)?.toDomain()
    }

    override suspend fun getProgressByDictionary(dictionaryId: String): List<LearningProgress> {
        return learningProgressDao.getProgressByDictionary(dictionaryId).map { it.toDomain() }
    }

    override suspend fun getLearnedWordCount(dictionaryId: String): Int {
        return learningProgressDao.getLearnedWordCount(dictionaryId)
    }

    override suspend fun saveProgress(progress: LearningProgress) {
        learningProgressDao.insertProgress(LearningProgressEntity.fromDomain(progress))
    }

    override suspend fun deleteProgress(wordId: String) {
        learningProgressDao.deleteProgressByWordId(wordId)
    }

    /**
     * SM-2 Spaced Repetition Algorithm
     * 
     * Quality ratings:
     * 0 - Complete blackout, no recall
     * 1 - Incorrect response, but upon seeing the correct answer it felt familiar
     * 2 - Incorrect response, but correct answer seemed easy to recall
     * 3 - Correct response with serious difficulty
     * 4 - Correct response after hesitation
     * 5 - Perfect response
     */
    override suspend fun recordAnswer(wordId: String, quality: Int): LearningProgress {
        val existingProgress = learningProgressDao.getProgressByWordId(wordId)?.toDomain()
        val now = LocalDateTime.now()
        
        val progress = if (existingProgress != null) {
            calculateNextReview(existingProgress, quality, now)
        } else {
            // First time reviewing this word
            val initialEaseFactor = 2.5f
            val (newLevel, newEaseFactor, intervalDays) = calculateSM2(0, initialEaseFactor, quality)
            
            LearningProgress(
                id = UUID.randomUUID().toString(),
                wordId = wordId,
                correctCount = if (quality >= 3) 1 else 0,
                incorrectCount = if (quality < 3) 1 else 0,
                level = newLevel,
                easeFactor = newEaseFactor,
                lastReviewed = now,
                nextReview = now.plusDays(intervalDays.toLong())
            )
        }
        
        learningProgressDao.insertProgress(LearningProgressEntity.fromDomain(progress))
        return progress
    }

    private fun calculateNextReview(
        current: LearningProgress,
        quality: Int,
        now: LocalDateTime
    ): LearningProgress {
        val (newLevel, newEaseFactor, intervalDays) = calculateSM2(
            current.level,
            current.easeFactor,
            quality
        )
        
        return current.copy(
            correctCount = if (quality >= 3) current.correctCount + 1 else current.correctCount,
            incorrectCount = if (quality < 3) current.incorrectCount + 1 else current.incorrectCount,
            level = newLevel,
            easeFactor = newEaseFactor,
            lastReviewed = now,
            nextReview = now.plusDays(intervalDays.toLong())
        )
    }

    /**
     * SM-2 algorithm calculation
     * Returns: Triple(newLevel, newEaseFactor, intervalDays)
     */
    private fun calculateSM2(
        currentLevel: Int,
        currentEaseFactor: Float,
        quality: Int
    ): Triple<Int, Float, Int> {
        val clampedQuality = quality.coerceIn(0, 5)
        
        // Calculate new ease factor
        val newEaseFactor = if (clampedQuality >= 3) {
            val ef = currentEaseFactor + (0.1f - (5 - clampedQuality) * (0.08f + (5 - clampedQuality) * 0.02f))
            max(1.3f, ef) // Minimum ease factor is 1.3
        } else {
            currentEaseFactor // Don't change ease factor on incorrect answers
        }
        
        // Calculate new level and interval
        val (newLevel, intervalDays) = if (clampedQuality < 3) {
            // Incorrect answer - reset to level 0
            Pair(0, 1)
        } else {
            when (currentLevel) {
                0 -> Pair(1, 1)
                1 -> Pair(2, 6)
                else -> {
                    val newLvl = currentLevel + 1
                    // Calculate interval: previous_interval * ease_factor
                    val prevInterval = when (currentLevel) {
                        2 -> 6
                        else -> (6 * Math.pow(currentEaseFactor.toDouble(), (currentLevel - 2).toDouble())).roundToInt()
                    }
                    val interval = (prevInterval * newEaseFactor).roundToInt()
                    Pair(newLvl, interval)
                }
            }
        }
        
        return Triple(newLevel, newEaseFactor, intervalDays)
    }
}

