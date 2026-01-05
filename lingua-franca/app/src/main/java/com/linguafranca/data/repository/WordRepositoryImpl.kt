package com.linguafranca.data.repository

import com.linguafranca.data.local.dao.LearningProgressDao
import com.linguafranca.data.local.dao.TagDao
import com.linguafranca.data.local.dao.WordDao
import com.linguafranca.data.local.entity.WordEntity
import com.linguafranca.domain.model.Word
import com.linguafranca.domain.model.WordWithProgress
import com.linguafranca.domain.model.WordWithTags
import com.linguafranca.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    private val wordDao: WordDao,
    private val tagDao: TagDao,
    private val learningProgressDao: LearningProgressDao
) : WordRepository {

    override fun observeWordsByDictionary(dictionaryId: String): Flow<List<Word>> {
        return wordDao.observeWordsByDictionary(dictionaryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeWordById(wordId: String): Flow<Word?> {
        return wordDao.observeWordById(wordId).map { it?.toDomain() }
    }

    override fun searchWordsInDictionary(dictionaryId: String, query: String): Flow<List<Word>> {
        return wordDao.searchWordsInDictionary(dictionaryId, query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchAllWords(userId: String, query: String): Flow<List<Word>> {
        return wordDao.searchAllWords(userId, query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWordsByTag(tagId: String): Flow<List<Word>> {
        return wordDao.getWordsByTag(tagId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWordsByTags(userId: String, tagIds: List<String>): Flow<List<Word>> {
        return wordDao.getWordsByTags(userId, tagIds).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getWordsByDictionary(dictionaryId: String): List<Word> {
        return wordDao.getWordsByDictionary(dictionaryId).map { it.toDomain() }
    }

    override suspend fun getWordById(wordId: String): Word? {
        return wordDao.getWordById(wordId)?.toDomain()
    }

    override suspend fun getWordCount(dictionaryId: String): Int {
        return wordDao.getWordCountByDictionary(dictionaryId)
    }

    override suspend fun createWord(word: Word) {
        wordDao.insertWord(WordEntity.fromDomain(word))
    }

    override suspend fun updateWord(word: Word) {
        wordDao.updateWord(WordEntity.fromDomain(word))
    }

    override suspend fun deleteWord(wordId: String) {
        wordDao.deleteWordById(wordId)
    }

    override suspend fun getWordWithTags(wordId: String): WordWithTags? {
        val word = wordDao.getWordById(wordId)?.toDomain() ?: return null
        val tags = tagDao.getTagsForWord(wordId).map { it.toDomain() }
        return WordWithTags(word, tags)
    }

    override fun observeWordWithTags(wordId: String): Flow<WordWithTags?> {
        return combine(
            wordDao.observeWordById(wordId),
            tagDao.observeTagsForWord(wordId)
        ) { wordEntity, tagEntities ->
            wordEntity?.let { word ->
                WordWithTags(
                    word = word.toDomain(),
                    tags = tagEntities.map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun updateWordTags(wordId: String, tagIds: List<String>) {
        wordDao.updateWordTags(wordId, tagIds)
    }

    override suspend fun getWordsForLearning(
        userId: String,
        dictionaryIds: List<String>?,
        limit: Int
    ): List<WordWithProgress> {
        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000
        
        val wordIds = if (dictionaryIds != null) {
            learningProgressDao.getWordsDueFromDictionaries(dictionaryIds, currentTime, limit)
        } else {
            learningProgressDao.getWordsDueForReview(userId, currentTime, limit)
        }
        
        return wordIds.mapNotNull { wordId ->
            val word = wordDao.getWordById(wordId)?.toDomain() ?: return@mapNotNull null
            val tags = tagDao.getTagsForWord(wordId).map { it.toDomain() }
            val progress = learningProgressDao.getProgressByWordId(wordId)?.toDomain()
            WordWithProgress(word, tags, progress)
        }
    }
}

