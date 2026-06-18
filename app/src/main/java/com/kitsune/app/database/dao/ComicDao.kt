package com.kitsune.app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kitsune.app.database.entity.ComicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {
    @Query("SELECT * FROM comics ORDER BY title ASC")
    fun getAllComics(): Flow<List<ComicEntity>>

    @Query("SELECT * FROM comics")
    suspend fun getAllComicsSync(): List<ComicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComics(comics: List<ComicEntity>)

    @Query("DELETE FROM comics WHERE relativePath = :path")
    suspend fun deleteByPath(path: String)

    @Query("DELETE FROM comics WHERE relativePath IN (:paths)")
    suspend fun deleteByPaths(paths: List<String>)

    @Transaction
    suspend fun updateLibrary(toInsert: List<ComicEntity>, toDelete: List<String>) {
        deleteByPaths(toDelete)
        insertComics(toInsert)
    }
}
