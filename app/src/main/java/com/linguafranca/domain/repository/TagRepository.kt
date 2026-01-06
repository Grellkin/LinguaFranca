package com.linguafranca.domain.repository

import com.linguafranca.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun observeTags(userId: String): Flow<List<Tag>>
    fun observeTagsForWord(wordId: String): Flow<List<Tag>>
    fun searchTags(userId: String, query: String): Flow<List<Tag>>
    
    suspend fun getTags(userId: String): List<Tag>
    suspend fun getTagsForWord(wordId: String): List<Tag>
    suspend fun getTagById(tagId: String): Tag?
    
    suspend fun createTag(tag: Tag)
    suspend fun updateTag(tag: Tag)
    suspend fun deleteTag(tagId: String)
}

