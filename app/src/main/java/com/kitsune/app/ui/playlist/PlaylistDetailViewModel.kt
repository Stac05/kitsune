package com.kitsune.app.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.data.repository.PlaylistRepository
import com.kitsune.app.data.repository.ScannerRepository
import com.kitsune.app.data.repository.SettingsRepository
import com.kitsune.app.domain.model.Comic
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    private val playlistId: Long,
    private val playlistRepository: PlaylistRepository,
    private val scannerRepository: ScannerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistDetailUiState>(PlaylistDetailUiState.Loading)
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    init {
        loadPlaylistDetail()
    }

    private fun loadPlaylistDetail() {
        viewModelScope.launch {
            val playlist = playlistRepository.getPlaylistById(playlistId)
            if (playlist == null) {
                _uiState.value = PlaylistDetailUiState.Error("Playlist not found")
                return@launch
            }

            combine(
                playlistRepository.getComicsInPlaylist(playlistId),
                scannerRepository.allComics,
                settingsRepository.settings.map { it?.gridSize ?: 3 }
            ) { playlistPaths, allComics, gridSize ->
                // Filter and sort comics based on their position in the playlist if we had a full model,
                // but since getComicsInPlaylist returns paths in order, we map them.
                val comicMap = allComics.associateBy { it.relativePath }
                val sortedComics = playlistPaths.mapNotNull { comicMap[it] }

                PlaylistDetailUiState.Success(
                    playlistName = playlist.name,
                    comics = sortedComics,
                    gridSize = gridSize
                )
            }.catch { e ->
                _uiState.value = PlaylistDetailUiState.Error(e.message ?: "Unknown error")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun renamePlaylist(newName: String) {
        viewModelScope.launch {
            playlistRepository.renamePlaylist(playlistId, newName)
        }
    }

    fun removeComic(comicPath: String) {
        viewModelScope.launch {
            playlistRepository.removeComicFromPlaylist(playlistId, comicPath)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }
}

sealed class PlaylistDetailUiState {
    data object Loading : PlaylistDetailUiState()
    data class Success(
        val playlistName: String,
        val comics: List<Comic>,
        val gridSize: Int
    ) : PlaylistDetailUiState()
    data class Error(val message: String) : PlaylistDetailUiState()
}
