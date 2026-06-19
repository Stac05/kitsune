package com.kitsune.app.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.data.repository.PlaylistRepository
import com.kitsune.app.data.repository.PlaylistWithCount
import com.kitsune.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola daftar Playlist.
 */
class PlaylistViewModel(
    private val playlistRepository: PlaylistRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistUiState>(PlaylistUiState.Loading)
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    init {
        loadPlaylists()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            combine(
                playlistRepository.getAllPlaylistsWithCount(),
                settingsRepository.settings.map { it?.gridSize ?: 3 }
            ) { playlists, gridSize ->
                if (playlists.isEmpty()) {
                    PlaylistUiState.Empty
                } else {
                    PlaylistUiState.Success(playlists, gridSize)
                }
            }.catch { e ->
                _uiState.value = PlaylistUiState.Error(e.message ?: "Unknown error")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
        }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(id)
        }
    }
}

sealed class PlaylistUiState {
    data object Loading : PlaylistUiState()
    data object Empty : PlaylistUiState()
    data class Success(val playlists: List<PlaylistWithCount>, val gridSize: Int) : PlaylistUiState()
    data class Error(val message: String) : PlaylistUiState()
}
