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
 * Mendukung Selection Mode untuk pengelolaan massal.
 */
class PlaylistViewModel(
    private val playlistRepository: PlaylistRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaylistUiState>(PlaylistUiState.Loading)
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    // Selection Mode State
    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    val selectionMode: StateFlow<Boolean> = _selectedIds
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

    fun toggleSelection(id: Long) {
        val current = _selectedIds.value
        if (current.contains(id)) {
            _selectedIds.value = current - id
        } else {
            _selectedIds.value = current + id
        }
    }

    fun selectAll() {
        val state = _uiState.value
        if (state is PlaylistUiState.Success) {
            _selectedIds.value = state.playlists.map { it.playlist.id }.toSet()
        }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelected() {
        val ids = _selectedIds.value.toList()
        if (ids.isEmpty()) return

        viewModelScope.launch {
            playlistRepository.deletePlaylists(ids)
            clearSelection()
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
