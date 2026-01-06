package com.linguafranca.domain.repository

import com.linguafranca.domain.model.Dictionary
import com.linguafranca.domain.model.DictionaryProgress
import com.linguafranca.domain.model.DictionaryType
import com.linguafranca.domain.model.DictionaryWithProgress
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    fun observeDictionaries(userId: String): Flow<List<Dictionary>>
    fun observeActiveDictionaries(userId: String): Flow<List<Dictionary>>
    fun observeDictionariesByType(userId: String, type: DictionaryType): Flow<List<Dictionary>>
    fun observeDictionaryById(dictionaryId: String): Flow<Dictionary?>
    fun searchDictionaries(userId: String, query: String): Flow<List<Dictionary>>
    
    suspend fun getDictionaries(userId: String): List<Dictionary>
    suspend fun getDictionaryById(dictionaryId: String): Dictionary?
    
    suspend fun createDictionary(dictionary: Dictionary)
    suspend fun updateDictionary(dictionary: Dictionary)
    suspend fun deleteDictionary(dictionaryId: String)
    suspend fun setDictionaryActive(dictionaryId: String, isActive: Boolean)
    
    suspend fun getDictionaryProgress(dictionaryId: String): DictionaryProgress
    fun observeDictionaryProgress(dictionaryId: String): Flow<DictionaryProgress>
    fun observeDictionariesWithProgress(userId: String): Flow<List<DictionaryWithProgress>>
}

