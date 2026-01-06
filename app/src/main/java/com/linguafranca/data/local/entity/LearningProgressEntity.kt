package com.linguafranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.linguafranca.domain.model.LearningProgress
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(
    tableName = "learning_progress",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("wordId", unique = true)]
)
data class LearningProgressEntity(
    @PrimaryKey
    val id: String,
    val wordId: String,
    val correctCount: Int,
    val incorrectCount: Int,
    val level: Int,
    val easeFactor: Float,
    val lastReviewed: Long?, // Epoch millis
    val nextReview: Long?    // Epoch millis
) {
    fun toDomain() = LearningProgress(
        id = id,
        wordId = wordId,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        level = level,
        easeFactor = easeFactor,
        lastReviewed = lastReviewed?.let { 
            LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneOffset.UTC) 
        },
        nextReview = nextReview?.let { 
            LocalDateTime.ofEpochSecond(it / 1000, 0, ZoneOffset.UTC) 
        }
    )

    companion object {
        fun fromDomain(progress: LearningProgress) = LearningProgressEntity(
            id = progress.id,
            wordId = progress.wordId,
            correctCount = progress.correctCount,
            incorrectCount = progress.incorrectCount,
            level = progress.level,
            easeFactor = progress.easeFactor,
            lastReviewed = progress.lastReviewed?.toEpochSecond(ZoneOffset.UTC)?.times(1000),
            nextReview = progress.nextReview?.toEpochSecond(ZoneOffset.UTC)?.times(1000)
        )
    }
}

