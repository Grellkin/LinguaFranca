package com.linguafranca.domain.model

import java.time.LocalDateTime

data class Dictionary(
    val id: String,
    val userId: String,
    val name: String,
    val description: String = "",
    val type: DictionaryType,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class DictionaryType {
    CUSTOM,    // User-created dictionaries
    FRANCO,    // App-provided predefined dictionaries
    COMMUNITY  // Dictionaries from other users (future)
}

