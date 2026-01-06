package com.linguafranca.domain.model

import java.time.LocalDateTime

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

