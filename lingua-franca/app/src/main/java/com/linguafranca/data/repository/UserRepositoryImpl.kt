package com.linguafranca.data.repository

import com.linguafranca.data.local.dao.UserDao
import com.linguafranca.data.local.dao.UserSettingsDao
import com.linguafranca.data.local.entity.UserEntity
import com.linguafranca.data.local.entity.UserSettingsEntity
import com.linguafranca.domain.model.User
import com.linguafranca.domain.model.UserSettings
import com.linguafranca.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userSettingsDao: UserSettingsDao
) : UserRepository {

    override fun observeCurrentUser(): Flow<User?> {
        return userDao.observeCurrentUser().map { it?.toDomain() }
    }

    override suspend fun getCurrentUser(): User? {
        return userDao.getCurrentUser()?.toDomain()
    }

    override suspend fun createUser(user: User) {
        userDao.insertUser(UserEntity.fromDomain(user))
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(UserEntity.fromDomain(user))
    }

    override suspend fun deleteUser(userId: String) {
        userDao.getUserById(userId)?.let { userDao.deleteUser(it) }
    }

    override fun observeUserSettings(userId: String): Flow<UserSettings?> {
        return userSettingsDao.observeSettingsByUserId(userId).map { it?.toDomain() }
    }

    override suspend fun getUserSettings(userId: String): UserSettings? {
        return userSettingsDao.getSettingsByUserId(userId)?.toDomain()
    }

    override suspend fun saveUserSettings(settings: UserSettings) {
        userSettingsDao.insertSettings(UserSettingsEntity.fromDomain(settings))
    }
}

