package com.linguafranca.domain.model

import java.time.LocalDateTime

data class Word(
    val id: String,
    val dictionaryId: String,
    val original: String,
    val mainTranslation: String,
    val additionalTranslations: List<String> = emptyList(),
    val examples: Map<String, String?> = emptyMap(),
    val notes: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Returns all translations (main + additional) for validation in learning mode
     */
    val allTranslations: List<String>
        get() = listOf(mainTranslation) + additionalTranslations

    /**
     * Checks if user input matches any of the translations (case-insensitive)
     */
    fun isCorrectAnswer(userInput: String): Boolean {
        val normalizedInput = userInput.trim().lowercase()
        return allTranslations.any { it.trim().lowercase() == normalizedInput }
    }

    /**
     * Checks if user is typing a correct answer (for real-time feedback)
     */
    fun isPartialMatch(userInput: String): Boolean {
        val normalizedInput = userInput.trim().lowercase()
        return normalizedInput.isNotEmpty() && allTranslations.any {
            it.trim().lowercase().startsWith(normalizedInput)
        }
    }
}

