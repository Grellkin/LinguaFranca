package com.linguafranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.linguafranca.data.local.entity.DictionaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DictionaryDao {
    @Query("SELECT * FROM dictionaries WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeDictionariesByUserId(userId: String): Flow<List<DictionaryEntity>>

    @Query("SELECT * FROM dictionaries WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getDictionariesByUserId(userId: String): List<DictionaryEntity>

    @Query("SELECT * FROM dictionaries WHERE userId = :userId AND isActive = 1 ORDER BY createdAt DESC")
    fun observeActiveDictionaries(userId: String): Flow<List<DictionaryEntity>>

    @Query("SELECT * FROM dictionaries WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    fun observeDictionariesByType(userId: String, type: String): Flow<List<DictionaryEntity>>

    @Query("SELECT * FROM dictionaries WHERE id = :dictionaryId")
    suspend fun getDictionaryById(dictionaryId: String): DictionaryEntity?

    @Query("SELECT * FROM dictionaries WHERE id = :dictionaryId")
    fun observeDictionaryById(dictionaryId: String): Flow<DictionaryEntity?>

    @Query("SELECT * FROM dictionaries WHERE userId = :userId AND name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchDictionaries(userId: String, query: String): Flow<List<DictionaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDictionary(dictionary: DictionaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDictionaries(dictionaries: List<DictionaryEntity>)

    @Update
    suspend fun updateDictionary(dictionary: DictionaryEntity)

    @Delete
    suspend fun deleteDictionary(dictionary: DictionaryEntity)

    @Query("DELETE FROM dictionaries WHERE id = :dictionaryId")
    suspend fun deleteDictionaryById(dictionaryId: String)

    @Query("UPDATE dictionaries SET isActive = :isActive WHERE id = :dictionaryId")
    suspend fun setDictionaryActive(dictionaryId: String, isActive: Boolean)

    @Query("UPDATE dictionaries SET updatedAt = :timestamp WHERE id = :dictionaryId")
    suspend fun touchDictionary(dictionaryId: String, timestamp: Long = System.currentTimeMillis())
}

