package com.linguafranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.linguafranca.data.local.entity.WordEntity
import com.linguafranca.data.local.entity.WordTagCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words WHERE dictionaryId = :dictionaryId ORDER BY createdAt DESC")
    fun observeWordsByDictionary(dictionaryId: String): Flow<List<WordEntity>>

    @Query("SELECT * FROM words WHERE dictionaryId = :dictionaryId ORDER BY createdAt DESC")
    suspend fun getWordsByDictionary(dictionaryId: String): List<WordEntity>

    @Query("SELECT * FROM words WHERE id = :wordId")
    suspend fun getWordById(wordId: String): WordEntity?

    @Query("SELECT * FROM words WHERE id = :wordId")
    fun observeWordById(wordId: String): Flow<WordEntity?>

    @Query("""
        SELECT * FROM words 
        WHERE dictionaryId = :dictionaryId 
        AND (
            original LIKE '%' || :query || '%' 
            OR mainTranslation LIKE '%' || :query || '%'
            OR additionalTranslations LIKE '%' || :query || '%'
        )
        ORDER BY createdAt DESC
    """)
    fun searchWordsInDictionary(dictionaryId: String, query: String): Flow<List<WordEntity>>

    @Query("""
        SELECT w.* FROM words w
        INNER JOIN dictionaries d ON w.dictionaryId = d.id
        WHERE d.userId = :userId
        AND (
            w.original LIKE '%' || :query || '%' 
            OR w.mainTranslation LIKE '%' || :query || '%'
            OR w.additionalTranslations LIKE '%' || :query || '%'
        )
        ORDER BY w.createdAt DESC
    """)
    fun searchAllWords(userId: String, query: String): Flow<List<WordEntity>>

    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_tag_cross_ref wt ON w.id = wt.wordId
        WHERE wt.tagId = :tagId
        ORDER BY w.createdAt DESC
    """)
    fun getWordsByTag(tagId: String): Flow<List<WordEntity>>

    @Query("""
        SELECT w.* FROM words w
        INNER JOIN word_tag_cross_ref wt ON w.id = wt.wordId
        INNER JOIN dictionaries d ON w.dictionaryId = d.id
        WHERE d.userId = :userId AND wt.tagId IN (:tagIds)
        ORDER BY w.createdAt DESC
    """)
    fun getWordsByTags(userId: String, tagIds: List<String>): Flow<List<WordEntity>>

    @Query("SELECT COUNT(*) FROM words WHERE dictionaryId = :dictionaryId")
    suspend fun getWordCountByDictionary(dictionaryId: String): Int

    @Query("SELECT COUNT(*) FROM words WHERE dictionaryId = :dictionaryId")
    fun observeWordCountByDictionary(dictionaryId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Update
    suspend fun updateWord(word: WordEntity)

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("DELETE FROM words WHERE id = :wordId")
    suspend fun deleteWordById(wordId: String)

    // Word-Tag relationships
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordTagCrossRef(crossRef: WordTagCrossRef)

    @Delete
    suspend fun deleteWordTagCrossRef(crossRef: WordTagCrossRef)

    @Query("DELETE FROM word_tag_cross_ref WHERE wordId = :wordId")
    suspend fun deleteAllTagsFromWord(wordId: String)

    @Query("SELECT tagId FROM word_tag_cross_ref WHERE wordId = :wordId")
    suspend fun getTagIdsForWord(wordId: String): List<String>

    @Transaction
    suspend fun updateWordTags(wordId: String, tagIds: List<String>) {
        deleteAllTagsFromWord(wordId)
        tagIds.forEach { tagId ->
            insertWordTagCrossRef(WordTagCrossRef(wordId, tagId))
        }
    }
}

