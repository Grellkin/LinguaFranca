package com.linguafranca.di

import com.linguafranca.data.repository.DictionaryRepositoryImpl
import com.linguafranca.data.repository.LearningRepositoryImpl
import com.linguafranca.data.repository.TagRepositoryImpl
import com.linguafranca.data.repository.UserRepositoryImpl
import com.linguafranca.data.repository.WordRepositoryImpl
import com.linguafranca.domain.repository.DictionaryRepository
import com.linguafranca.domain.repository.LearningRepository
import com.linguafranca.domain.repository.TagRepository
import com.linguafranca.domain.repository.UserRepository
import com.linguafranca.domain.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindDictionaryRepository(impl: DictionaryRepositoryImpl): DictionaryRepository

    @Binds
    @Singleton
    abstract fun bindWordRepository(impl: WordRepositoryImpl): WordRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: TagRepositoryImpl): TagRepository

    @Binds
    @Singleton
    abstract fun bindLearningRepository(impl: LearningRepositoryImpl): LearningRepository
}

