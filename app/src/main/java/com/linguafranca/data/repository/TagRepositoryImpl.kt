package com.linguafranca.data.repository

import com.linguafranca.data.local.dao.TagDao
import com.linguafranca.data.local.entity.TagEntity
import com.linguafranca.domain.model.Tag
import com.linguafranca.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override fun observeTags(userId: String): Flow<List<Tag>> {
        return tagDao.observeTagsByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTagsForWord(wordId: String): Flow<List<Tag>> {
        return tagDao.observeTagsForWord(wordId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchTags(userId: String, query: String): Flow<List<Tag>> {
        return tagDao.searchTags(userId, query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTags(userId: String): List<Tag> {
        return tagDao.getTagsByUserId(userId).map { it.toDomain() }
    }

    override suspend fun getTagsForWord(wordId: String): List<Tag> {
        return tagDao.getTagsForWord(wordId).map { it.toDomain() }
    }

    override suspend fun getTagById(tagId: String): Tag? {
        return tagDao.getTagById(tagId)?.toDomain()
    }

    override suspend fun createTag(tag: Tag) {
        tagDao.insertTag(TagEntity.fromDomain(tag))
    }

    override suspend fun updateTag(tag: Tag) {
        tagDao.updateTag(TagEntity.fromDomain(tag))
    }

    override suspend fun deleteTag(tagId: String) {
        tagDao.deleteTagById(tagId)
    }
}

