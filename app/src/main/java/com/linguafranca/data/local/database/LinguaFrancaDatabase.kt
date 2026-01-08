package com.linguafranca.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.linguafranca.data.local.dao.DictionaryDao
import com.linguafranca.data.local.dao.LearningProgressDao
import com.linguafranca.data.local.dao.TagDao
import com.linguafranca.data.local.dao.UserDao
import com.linguafranca.data.local.dao.UserSettingsDao
import com.linguafranca.data.local.dao.WordDao
import com.linguafranca.data.local.entity.DictionaryEntity
import com.linguafranca.data.local.entity.LearningProgressEntity
import com.linguafranca.data.local.entity.TagEntity
import com.linguafranca.data.local.entity.UserEntity
import com.linguafranca.data.local.entity.UserSettingsEntity
import com.linguafranca.data.local.entity.WordEntity
import com.linguafranca.data.local.entity.WordTagCrossRef

@Database(
    entities = [
        UserEntity::class,
        UserSettingsEntity::class,
        DictionaryEntity::class,
        WordEntity::class,
        TagEntity::class,
        WordTagCrossRef::class,
        LearningProgressEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class LinguaFrancaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun dictionaryDao(): DictionaryDao
    abstract fun wordDao(): WordDao
    abstract fun tagDao(): TagDao
    abstract fun learningProgressDao(): LearningProgressDao

    companion object {
        const val DATABASE_NAME = "lingua_franca_db"
    }
}

