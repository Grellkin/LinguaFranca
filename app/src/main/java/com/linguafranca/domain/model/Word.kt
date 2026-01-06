package com.linguafranca.domain.model

import java.time.LocalDateTime

data class Word(
    val id: String,
    val dictionaryId: String,
    val original: String,
    val translation: String,
    val notes: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
)

