package com.linguafranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.linguafranca.domain.model.Tag

@Entity(
    tableName = "tags",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class TagEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val color: String
) {
    fun toDomain() = Tag(
        id = id,
        userId = userId,
        name = name,
        color = color
    )

    companion object {
        fun fromDomain(tag: Tag) = TagEntity(
            id = tag.id,
            userId = tag.userId,
            name = tag.name,
            color = tag.color
        )
    }
}

