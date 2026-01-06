package com.linguafranca.domain.repository

import com.linguafranca.domain.model.User
import com.linguafranca.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun getCurrentUser(): User?
    suspend fun createUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(userId: String)
    
    fun observeUserSettings(userId: String): Flow<UserSettings?>
    suspend fun getUserSettings(userId: String): UserSettings?
    suspend fun saveUserSettings(settings: UserSettings)
}

