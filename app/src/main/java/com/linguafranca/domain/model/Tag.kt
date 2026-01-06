package com.linguafranca.domain.model

data class Tag(
    val id: String,
    val userId: String,
    val name: String,
    val color: String = "#4CAF50" // Default green color
)

