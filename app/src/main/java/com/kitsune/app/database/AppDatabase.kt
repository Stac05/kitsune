package com.kitsune.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kitsune.app.database.dao.BookmarkDao
import com.kitsune.app.database.dao.ComicDao
import com.kitsune.app.database.dao.PlaylistDao
import com.kitsune.app.database.dao.ReadingProgressDao
import com.kitsune.app.database.dao.SettingsDao
import com.kitsune.app.database.entity.*

@Database(
    entities = [
        SettingsEntity::class,
        ComicEntity::class,
        ReadingProgressEntity::class,
        BookmarkEntity::class,
        BookmarkComicEntity::class,
        PlaylistEntity::class,
        PlaylistComicEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun comicDao(): ComicDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Buat tabel baru dengan skema yang benar
                db.execSQL("""
                    CREATE TABLE reading_progress_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        comicRelativePath TEXT NOT NULL,
                        chapterRelativePath TEXT NOT NULL,
                        pageNumber INTEGER NOT NULL,
                        totalPages INTEGER NOT NULL,
                        lastReadAt INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. Salin data dari tabel lama ke tabel baru
                db.execSQL("""
                    INSERT INTO reading_progress_new (id, comicRelativePath, chapterRelativePath, pageNumber, totalPages, lastReadAt)
                    SELECT id, comicRelativePath, chapterRelativePath, pageNumber, totalPages, lastReadAt FROM reading_progress
                """.trimIndent())

                // 3. Hapus tabel lama
                db.execSQL("DROP TABLE reading_progress")

                // 4. Ubah nama tabel baru menjadi nama tabel lama
                db.execSQL("ALTER TABLE reading_progress_new RENAME TO reading_progress")

                // 5. Buat index composite unik yang baru
                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_reading_progress_comicRelativePath_chapterRelativePath 
                    ON reading_progress (comicRelativePath, chapterRelativePath)
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitsune.db"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
