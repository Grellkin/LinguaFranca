package com.linguafranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.linguafranca.domain.model.UserSettings

@Entity(
    tableName = "user_settings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserSettingsEntity(
    @PrimaryKey
    val userId: String,
    val targetLanguage: String,
    val nativeLanguage: String,
    val darkTheme: Boolean
) {
    fun toDomain() = UserSettings(
        userId = userId,
        targetLanguage = targetLanguage,
        nativeLanguage = nativeLanguage,
        darkTheme = darkTheme
    )

    companion object {
        fun fromDomain(settings: UserSettings) = UserSettingsEntity(
            userId = settings.userId,
            targetLanguage = settings.targetLanguage,
            nativeLanguage = settings.nativeLanguage,
            darkTheme = settings.darkTheme
        )
    }
}

