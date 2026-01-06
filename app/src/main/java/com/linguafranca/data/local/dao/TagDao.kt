package com.linguafranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.linguafranca.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags WHERE userId = :userId ORDER BY name ASC")
    fun observeTagsByUserId(userId: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE userId = :userId ORDER BY name ASC")
    suspend fun getTagsByUserId(userId: String): List<TagEntity>

    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: String): TagEntity?

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN word_tag_cross_ref wt ON t.id = wt.tagId
        WHERE wt.wordId = :wordId
    """)
    suspend fun getTagsForWord(wordId: String): List<TagEntity>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN word_tag_cross_ref wt ON t.id = wt.tagId
        WHERE wt.wordId = :wordId
    """)
    fun observeTagsForWord(wordId: String): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE userId = :userId AND name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchTags(userId: String, query: String): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: TagEntity)

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTagById(tagId: String)
}

