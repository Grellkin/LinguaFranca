package com.linguafranca.domain.model

data class UserSettings(
    val userId: String,
    val targetLanguage: String = "en", // English
    val nativeLanguage: String = "ru", // Russian
    val darkTheme: Boolean = false
)

