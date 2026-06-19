package com.kitsune.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1,
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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kitsune.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
