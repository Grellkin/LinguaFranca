package com.linguafranca.domain.repository

import com.linguafranca.domain.model.LearningProgress
import kotlinx.coroutines.flow.Flow

interface LearningRepository {
    fun observeProgressByWordId(wordId: String): Flow<LearningProgress?>
    fun observeProgressByDictionary(dictionaryId: String): Flow<List<LearningProgress>>
    
    suspend fun getProgressByWordId(wordId: String): LearningProgress?
    suspend fun getProgressByDictionary(dictionaryId: String): List<LearningProgress>
    suspend fun getLearnedWordCount(dictionaryId: String): Int
    
    suspend fun saveProgress(progress: LearningProgress)
    suspend fun deleteProgress(wordId: String)
    
    // SM-2 algorithm - record answer and calculate next review
    suspend fun recordAnswer(wordId: String, quality: Int): LearningProgress
}

