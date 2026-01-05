package com.linguafranca.domain.repository

import com.linguafranca.domain.model.Tag
import com.linguafranca.domain.model.Word
import com.linguafranca.domain.model.WordWithProgress
import com.linguafranca.domain.model.WordWithTags
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun observeWordsByDictionary(dictionaryId: String): Flow<List<Word>>
    fun observeWordById(wordId: String): Flow<Word?>
    fun searchWordsInDictionary(dictionaryId: String, query: String): Flow<List<Word>>
    fun searchAllWords(userId: String, query: String): Flow<List<Word>>
    fun getWordsByTag(tagId: String): Flow<List<Word>>
    fun getWordsByTags(userId: String, tagIds: List<String>): Flow<List<Word>>
    
    suspend fun getWordsByDictionary(dictionaryId: String): List<Word>
    suspend fun getWordById(wordId: String): Word?
    suspend fun getWordCount(dictionaryId: String): Int
    
    suspend fun createWord(word: Word)
    suspend fun updateWord(word: Word)
    suspend fun deleteWord(wordId: String)
    
    // Word with tags
    suspend fun getWordWithTags(wordId: String): WordWithTags?
    fun observeWordWithTags(wordId: String): Flow<WordWithTags?>
    suspend fun updateWordTags(wordId: String, tagIds: List<String>)
    
    // Words for learning
    suspend fun getWordsForLearning(
        userId: String, 
        dictionaryIds: List<String>? = null,
        limit: Int = 20
    ): List<WordWithProgress>
}

