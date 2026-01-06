package com.linguafranca.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE dictionaries ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        database.execSQL("UPDATE dictionaries SET updatedAt = createdAt")
    }
}