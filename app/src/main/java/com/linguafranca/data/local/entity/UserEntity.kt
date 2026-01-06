package com.linguafranca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.linguafranca.domain.model.User
import java.time.LocalDateTime

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val displayName: String,
    val createdAt: Long // Epoch millis
) {
    fun toDomain() = User(
        id = id,
        email = email,
        displayName = displayName,
        createdAt = LocalDateTime.ofEpochSecond(createdAt / 1000, 0, java.time.ZoneOffset.UTC)
    )

    companion object {
        fun fromDomain(user: User) = UserEntity(
            id = user.id,
            email = user.email,
            displayName = user.displayName,
            createdAt = user.createdAt.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
        )
    }
}

