package com.linguafranca.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.linguafranca.data.local.entity.LearningProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningProgressDao {
    @Query("SELECT * FROM learning_progress WHERE wordId = :wordId")
    suspend fun getProgressByWordId(wordId: String): LearningProgressEntity?

    @Query("SELECT * FROM learning_progress WHERE wordId = :wordId")
    fun observeProgressByWordId(wordId: String): Flow<LearningProgressEntity?>

    @Query("""
        SELECT lp.* FROM learning_progress lp
        INNER JOIN words w ON lp.wordId = w.id
        WHERE w.dictionaryId = :dictionaryId
    """)
    fun observeProgressByDictionary(dictionaryId: String): Flow<List<LearningProgressEntity>>

    @Query("""
        SELECT lp.* FROM learning_progress lp
        INNER JOIN words w ON lp.wordId = w.id
        WHERE w.dictionaryId = :dictionaryId
    """)
    suspend fun getProgressByDictionary(dictionaryId: String): List<LearningProgressEntity>

    @Query("""
        SELECT COUNT(*) FROM learning_progress lp
        INNER JOIN words w ON lp.wordId = w.id
        WHERE w.dictionaryId = :dictionaryId AND lp.level >= :minLevel
    """)
    suspend fun getLearnedWordCount(dictionaryId: String, minLevel: Int = 3): Int

    @Query("""
        SELECT COUNT(*) FROM learning_progress lp
        INNER JOIN words w ON lp.wordId = w.id
        WHERE w.dictionaryId = :dictionaryId AND lp.level >= :minLevel
    """)
    fun observeLearnedWordCount(dictionaryId: String, minLevel: Int = 3): Flow<Int>

    // Get words due for review
    @Query("""
        SELECT w.id FROM words w
        INNER JOIN dictionaries d ON w.dictionaryId = d.id
        LEFT JOIN learning_progress lp ON w.id = lp.wordId
        WHERE d.userId = :userId 
        AND d.isActive = 1
        AND (lp.nextReview IS NULL OR lp.nextReview <= :currentTime)
        ORDER BY COALESCE(lp.nextReview, 0) ASC
        LIMIT :limit
    """)
    suspend fun getWordsDueForReview(userId: String, currentTime: Long, limit: Int = 20): List<String>

    // Get words due for review from specific dictionaries
    @Query("""
        SELECT w.id FROM words w
        LEFT JOIN learning_progress lp ON w.id = lp.wordId
        WHERE w.dictionaryId IN (:dictionaryIds)
        AND (lp.nextReview IS NULL OR lp.nextReview <= :currentTime)
        ORDER BY COALESCE(lp.nextReview, 0) ASC
        LIMIT :limit
    """)
    suspend fun getWordsDueFromDictionaries(
        dictionaryIds: List<String>, 
        currentTime: Long, 
        limit: Int = 20
    ): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LearningProgressEntity)

    @Update
    suspend fun updateProgress(progress: LearningProgressEntity)

    @Query("DELETE FROM learning_progress WHERE wordId = :wordId")
    suspend fun deleteProgressByWordId(wordId: String)

    @Query("DELETE FROM learning_progress WHERE id = :progressId")
    suspend fun deleteProgressById(progressId: String)
}

