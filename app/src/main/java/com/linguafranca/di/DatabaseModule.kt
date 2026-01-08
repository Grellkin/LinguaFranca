package com.linguafranca.di

import android.content.Context
import androidx.room.Room
import com.linguafranca.data.local.dao.DictionaryDao
import com.linguafranca.data.local.dao.LearningProgressDao
import com.linguafranca.data.local.dao.TagDao
import com.linguafranca.data.local.dao.UserDao
import com.linguafranca.data.local.dao.UserSettingsDao
import com.linguafranca.data.local.dao.WordDao
import com.linguafranca.data.local.database.LinguaFrancaDatabase
import com.linguafranca.data.local.database.MIGRATION_1_2
import com.linguafranca.data.local.database.MIGRATION_2_3
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LinguaFrancaDatabase {
        return Room.databaseBuilder(
            context,
            LinguaFrancaDatabase::class.java,
            LinguaFrancaDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideUserDao(database: LinguaFrancaDatabase): UserDao = database.userDao()

    @Provides
    fun provideUserSettingsDao(database: LinguaFrancaDatabase): UserSettingsDao = database.userSettingsDao()

    @Provides
    fun provideDictionaryDao(database: LinguaFrancaDatabase): DictionaryDao = database.dictionaryDao()

    @Provides
    fun provideWordDao(database: LinguaFrancaDatabase): WordDao = database.wordDao()

    @Provides
    fun provideTagDao(database: LinguaFrancaDatabase): TagDao = database.tagDao()

    @Provides
    fun provideLearningProgressDao(database: LinguaFrancaDatabase): LearningProgressDao = database.learningProgressDao()
}

