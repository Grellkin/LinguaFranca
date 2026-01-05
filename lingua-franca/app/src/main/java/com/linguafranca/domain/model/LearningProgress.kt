package com.linguafranca.domain.model

import java.time.LocalDateTime

data class LearningProgress(
    val id: String,
    val wordId: String,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val level: Int = 0, // 0-5 based on SM-2 algorithm
    val easeFactor: Float = 2.5f, // SM-2 ease factor
    val lastReviewed: LocalDateTime? = null,
    val nextReview: LocalDateTime? = null
)

data class DictionaryProgress(
    val dictionaryId: String,
    val totalWords: Int,
    val learnedWords: Int, // Words at level >= 3
    val progressPercent: Float
)

