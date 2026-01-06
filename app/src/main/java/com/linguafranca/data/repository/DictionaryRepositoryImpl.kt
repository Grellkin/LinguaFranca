package com.linguafranca.data.repository

import com.linguafranca.data.local.dao.DictionaryDao
import com.linguafranca.data.local.dao.LearningProgressDao
import com.linguafranca.data.local.dao.WordDao
import com.linguafranca.data.local.entity.DictionaryEntity
import com.linguafranca.domain.model.Dictionary
import com.linguafranca.domain.model.DictionaryProgress
import com.linguafranca.domain.model.DictionaryType
import com.linguafranca.domain.model.DictionaryWithProgress
import com.linguafranca.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepositoryImpl @Inject constructor(
    private val dictionaryDao: DictionaryDao,
    private val wordDao: WordDao,
    private val learningProgressDao: LearningProgressDao
) : DictionaryRepository {

    override fun observeDictionaries(userId: String): Flow<List<Dictionary>> {
        return dictionaryDao.observeDictionariesByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeActiveDictionaries(userId: String): Flow<List<Dictionary>> {
        return dictionaryDao.observeActiveDictionaries(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDictionariesByType(userId: String, type: DictionaryType): Flow<List<Dictionary>> {
        return dictionaryDao.observeDictionariesByType(userId, type.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeDictionaryById(dictionaryId: String): Flow<Dictionary?> {
        return dictionaryDao.observeDictionaryById(dictionaryId).map { it?.toDomain() }
    }

    override fun searchDictionaries(userId: String, query: String): Flow<List<Dictionary>> {
        return dictionaryDao.searchDictionaries(userId, query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDictionaries(userId: String): List<Dictionary> {
        return dictionaryDao.getDictionariesByUserId(userId).map { it.toDomain() }
    }

    override suspend fun getDictionaryById(dictionaryId: String): Dictionary? {
        return dictionaryDao.getDictionaryById(dictionaryId)?.toDomain()
    }

    override suspend fun createDictionary(dictionary: Dictionary) {
        dictionaryDao.insertDictionary(DictionaryEntity.fromDomain(dictionary))
    }

    override suspend fun updateDictionary(dictionary: Dictionary) {
        dictionaryDao.updateDictionary(DictionaryEntity.fromDomain(dictionary))
    }

    override suspend fun deleteDictionary(dictionaryId: String) {
        dictionaryDao.deleteDictionaryById(dictionaryId)
    }

    override suspend fun setDictionaryActive(dictionaryId: String, isActive: Boolean) {
        dictionaryDao.setDictionaryActive(dictionaryId, isActive)
    }

    override suspend fun getDictionaryProgress(dictionaryId: String): DictionaryProgress {
        val totalWords = wordDao.getWordCountByDictionary(dictionaryId)
        val learnedWords = learningProgressDao.getLearnedWordCount(dictionaryId)
        val progressPercent = if (totalWords > 0) {
            (learnedWords.toFloat() / totalWords) * 100
        } else 0f
        
        return DictionaryProgress(
            dictionaryId = dictionaryId,
            totalWords = totalWords,
            learnedWords = learnedWords,
            progressPercent = progressPercent
        )
    }

    override fun observeDictionaryProgress(dictionaryId: String): Flow<DictionaryProgress> {
        return combine(
            wordDao.observeWordCountByDictionary(dictionaryId),
            learningProgressDao.observeLearnedWordCount(dictionaryId)
        ) { totalWords, learnedWords ->
            val progressPercent = if (totalWords > 0) {
                (learnedWords.toFloat() / totalWords) * 100
            } else 0f
            
            DictionaryProgress(
                dictionaryId = dictionaryId,
                totalWords = totalWords,
                learnedWords = learnedWords,
                progressPercent = progressPercent
            )
        }
    }

    override fun observeDictionariesWithProgress(userId: String): Flow<List<DictionaryWithProgress>> {
        return dictionaryDao.observeDictionariesByUserId(userId).map { entities ->
            entities.map { entity ->
                val dictionary = entity.toDomain()
                val progress = getDictionaryProgress(dictionary.id)
                DictionaryWithProgress(dictionary, progress)
            }
        }
    }
}

