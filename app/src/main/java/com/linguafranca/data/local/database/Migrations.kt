package com.linguafranca.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE dictionaries ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE dictionaries SET updatedAt = createdAt")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Rename 'translation' column to 'mainTranslation'
        // Add new columns: additionalTranslations (JSON), examples (JSON)
        // SQLite doesn't support ALTER TABLE RENAME COLUMN before version 3.25.0
        // So we need to recreate the table
        
        // 1. Create new table with updated schema
        db.execSQL("""
            CREATE TABLE words_new (
                id TEXT NOT NULL PRIMARY KEY,
                dictionaryId TEXT NOT NULL,
                original TEXT NOT NULL,
                mainTranslation TEXT NOT NULL,
                additionalTranslations TEXT,
                examples TEXT,
                notes TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                FOREIGN KEY(dictionaryId) REFERENCES dictionaries(id) ON DELETE CASCADE
            )
        """)
        
        // 2. Copy data from old table to new table
        db.execSQL("""
            INSERT INTO words_new (id, dictionaryId, original, mainTranslation, additionalTranslations, examples, notes, createdAt)
            SELECT id, dictionaryId, original, translation, NULL, NULL, notes, createdAt
            FROM words
        """)
        
        // 3. Drop old table
        db.execSQL("DROP TABLE words")
        
        // 4. Rename new table to original name
        db.execSQL("ALTER TABLE words_new RENAME TO words")
        
        // 5. Recreate index
        db.execSQL("CREATE INDEX index_words_dictionaryId ON words(dictionaryId)")
    }
}