package com.kitsune.app.data.repository

import com.kitsune.app.database.dao.PlaylistDao
import com.kitsune.app.database.entity.PlaylistComicEntity
import com.kitsune.app.database.entity.PlaylistEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * Repository untuk mengelola Playlist.
 */
class PlaylistRepository(private val playlistDao: PlaylistDao) {

    /**
     * Mendapatkan semua playlist beserta jumlah komiknya.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllPlaylistsWithCount(): Flow<List<PlaylistWithCount>> {
        return playlistDao.getAllPlaylists().flatMapLatest { playlists ->
            if (playlists.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flows = playlists.map { playlist ->
                    playlistDao.getComicCountInPlaylist(playlist.id).map { count ->
                        PlaylistWithCount(playlist, count)
                    }
                }
                combine(flows) { it.toList() }
            }
        }
    }

    suspend fun getPlaylistById(id: Long): PlaylistEntity? {
        return playlistDao.getPlaylistById(id)
    }

    fun getComicsInPlaylist(playlistId: Long): Flow<List<String>> {
        return playlistDao.getComicsInPlaylist(playlistId)
    }

    /**
     * Mendapatkan seluruh jalur relatif komik yang ada di kategori playlist manapun.
     */
    fun getAllPlaylistComics(): Flow<List<String>> {
        return playlistDao.getAllPlaylistComics()
    }

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    suspend fun renamePlaylist(id: Long, newName: String) {
        playlistDao.renamePlaylist(id, newName)
    }

    suspend fun deletePlaylist(id: Long) {
        playlistDao.deletePlaylist(id)
    }

    /**
     * Menghapus banyak playlist sekaligus.
     */
    suspend fun deletePlaylists(ids: List<Long>) {
        playlistDao.deletePlaylists(ids)
    }

    suspend fun addComicToPlaylist(playlistId: Long, comicPath: String) {
        val maxPos = playlistDao.getMaxPosition(playlistId) ?: -1
        playlistDao.addComicToPlaylist(
            PlaylistComicEntity(
                playlistId = playlistId,
                comicRelativePath = comicPath,
                position = maxPos + 1
            )
        )
    }

    /**
     * REVISION 5.2: Menambahkan banyak komik ke banyak playlist sekaligus secara batch.
     */
    suspend fun addComicsToPlaylists(playlistIds: List<Long>, comicPaths: List<String>) {
        val entities = mutableListOf<PlaylistComicEntity>()
        playlistIds.forEach { playlistId ->
            val maxPos = playlistDao.getMaxPosition(playlistId) ?: -1
            var currentPos = maxPos + 1
            comicPaths.forEach { path ->
                entities.add(
                    PlaylistComicEntity(
                        playlistId = playlistId,
                        comicRelativePath = path,
                        position = currentPos++
                    )
                )
            }
        }
        if (entities.isNotEmpty()) {
            playlistDao.addComicsToPlaylists(entities)
        }
    }

    suspend fun removeComicFromPlaylist(playlistId: Long, comicPath: String) {
        playlistDao.removeComicFromPlaylist(playlistId, comicPath)
    }

    /**
     * Menghapus banyak komik dari playlist tertentu sekaligus.
     */
    suspend fun removeComicsFromPlaylist(playlistId: Long, comicPaths: List<String>) {
        playlistDao.removeComicsFromPlaylist(playlistId, comicPaths)
    }

    fun isComicInPlaylist(playlistId: Long, comicPath: String): Flow<Boolean> {
        return playlistDao.isComicInPlaylist(playlistId, comicPath)
    }
}

data class PlaylistWithCount(
    val playlist: PlaylistEntity,
    val count: Int
)
