package com.kitsune.app.database.dao

import androidx.room.*
import com.kitsune.app.database.entity.PlaylistComicEntity
import com.kitsune.app.database.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?

    @Query("SELECT COUNT(*) FROM playlist_comics WHERE playlistId = :playlistId")
    fun getComicCountInPlaylist(playlistId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("UPDATE playlists SET name = :newName WHERE id = :playlistId")
    suspend fun renamePlaylist(playlistId: Long, newName: String)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("DELETE FROM playlists WHERE id IN (:playlistIds)")
    suspend fun deletePlaylists(playlistIds: List<Long>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addComicToPlaylist(playlistComic: PlaylistComicEntity)

    @Query("DELETE FROM playlist_comics WHERE playlistId = :playlistId AND comicRelativePath = :comicPath")
    suspend fun removeComicFromPlaylist(playlistId: Long, comicPath: String)

    @Query("DELETE FROM playlist_comics WHERE playlistId = :playlistId AND comicRelativePath IN (:comicPaths)")
    suspend fun removeComicsFromPlaylist(playlistId: Long, comicPaths: List<String>)

    @Query("SELECT comicRelativePath FROM playlist_comics WHERE playlistId = :playlistId ORDER BY position ASC, createdAt DESC")
    fun getComicsInPlaylist(playlistId: Long): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM playlist_comics WHERE playlistId = :playlistId AND comicRelativePath = :comicPath)")
    fun isComicInPlaylist(playlistId: Long, comicPath: String): Flow<Boolean>

    @Query("SELECT MAX(position) FROM playlist_comics WHERE playlistId = :playlistId")
    suspend fun getMaxPosition(playlistId: Long): Int?
}
