package com.kitsune.app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kitsune.app.database.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE comicRelativePath = :comicPath LIMIT 1")
    fun getProgressByComic(comicPath: String): Flow<ReadingProgressEntity?>

    @Query("SELECT * FROM reading_progress WHERE comicRelativePath = :comicPath LIMIT 1")
    suspend fun getProgressByComicSync(comicPath: String): ReadingProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress WHERE comicRelativePath = :comicPath")
    suspend fun deleteProgress(comicPath: String)
}
