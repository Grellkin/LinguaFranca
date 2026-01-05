package com.linguafranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.linguafranca.domain.model.Word
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(
    tableName = "words",
    foreignKeys = [
        ForeignKey(
            entity = DictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["dictionaryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dictionaryId")]
)
data class WordEntity(
    @PrimaryKey
    val id: String,
    val dictionaryId: String,
    val original: String,
    val translation: String,
    val notes: String,
    val createdAt: Long
) {
    fun toDomain() = Word(
        id = id,
        dictionaryId = dictionaryId,
        original = original,
        translation = translation,
        notes = notes,
        createdAt = LocalDateTime.ofEpochSecond(createdAt / 1000, 0, ZoneOffset.UTC)
    )

    companion object {
        fun fromDomain(word: Word) = WordEntity(
            id = word.id,
            dictionaryId = word.dictionaryId,
            original = word.original,
            translation = word.translation,
            notes = word.notes,
            createdAt = word.createdAt.toEpochSecond(ZoneOffset.UTC) * 1000
        )
    }
}

