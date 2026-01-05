package com.linguafranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.linguafranca.domain.model.Dictionary
import com.linguafranca.domain.model.DictionaryType
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(
    tableName = "dictionaries",
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
data class DictionaryEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val type: String, // CUSTOM, FRANCO, COMMUNITY
    val isActive: Boolean,
    val createdAt: Long
) {
    fun toDomain() = Dictionary(
        id = id,
        userId = userId,
        name = name,
        description = description,
        type = DictionaryType.valueOf(type),
        isActive = isActive,
        createdAt = LocalDateTime.ofEpochSecond(createdAt / 1000, 0, ZoneOffset.UTC)
    )

    companion object {
        fun fromDomain(dictionary: Dictionary) = DictionaryEntity(
            id = dictionary.id,
            userId = dictionary.userId,
            name = dictionary.name,
            description = dictionary.description,
            type = dictionary.type.name,
            isActive = dictionary.isActive,
            createdAt = dictionary.createdAt.toEpochSecond(ZoneOffset.UTC) * 1000
        )
    }
}

