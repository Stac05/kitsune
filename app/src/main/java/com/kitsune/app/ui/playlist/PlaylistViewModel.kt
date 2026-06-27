package com.kitsune.app.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.data.repository.BookmarkRepository
import com.kitsune.app.data.repository.PlaylistRepository
import com.kitsune.app.data.repository.ScannerRepository
import com.kitsune.app.data.repository.SettingsRepository
import com.kitsune.app.domain.model.Comic
import com.kitsune.app.database.entity.PlaylistEntity
import com.kitsune.app.ui.library.ComicStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola seluruh ekosistem Playlist (Kategori & Konten).
 */
class PlaylistViewModel(
    private val playlistRepository: PlaylistRepository,
    private val scannerRepository: ScannerRepository,
    private val settingsRepository: SettingsRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    // Selection Mode State
    private val _selectedPaths = MutableStateFlow<Set<String>>(emptySet())
    val selectedPaths: StateFlow<Set<String>> = _selectedPaths.asStateFlow()

    val selectionMode: StateFlow<Boolean> = _selectedPaths
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * Daftar seluruh kategori playlist yang ada.
     */
    val categories: StateFlow<List<PlaylistEntity>> = playlistRepository.getAllPlaylistsWithCount()
        .map { list -> list.map { it.playlist } }
        .onEach { list ->
            val currentId = _selectedCategoryId.value
            if (list.isNotEmpty()) {
                if (currentId == null || list.none { it.id == currentId }) {
                    _selectedCategoryId.value = list.first().id
                }
            } else {
                _selectedCategoryId.value = null
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * StateFlow untuk mendapatkan seluruh path komik yang dibookmark.
     */
    val bookmarkedPaths: StateFlow<Set<String>> = bookmarkRepository.getAllBookmarkedComics()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    /**
     * StateFlow untuk mendapatkan seluruh path komik yang masuk playlist.
     */
    val playlistPaths: StateFlow<Set<String>> = playlistRepository.getAllPlaylistComics()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PlaylistUiState> = combine(
        _selectedCategoryId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else playlistRepository.getComicsInPlaylist(id)
        },
        scannerRepository.allComics,
        _searchQuery,
        settingsRepository.settings,
        bookmarkedPaths,
        playlistPaths
    ) { array ->
        @Suppress("UNCHECKED_CAST")
        val currentCategoryPaths = array[0] as List<String>
        @Suppress("UNCHECKED_CAST")
        val allComics = array[1] as List<Comic>
        val query = array[2] as String
        val settings = array[3] as com.kitsune.app.database.entity.SettingsEntity?
        @Suppress("UNCHECKED_CAST")
        val bookmarks = array[4] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val playlists = array[5] as Set<String>

        val gridSize = settings?.gridSize ?: 3
        
        if (_selectedCategoryId.value == null) {
            return@combine PlaylistUiState.Empty
        }

        val comicMap = allComics.associateBy { it.relativePath }
        val comics = currentCategoryPaths.mapNotNull { comicMap[it] }
        
        val filteredComics = if (query.isBlank()) {
            comics
        } else {
            comics.filter { it.title.contains(query, ignoreCase = true) }
        }

        // Build status map
        val statusMap = filteredComics.associate { comic ->
            val path = comic.relativePath
            val statuses = mutableSetOf<ComicStatus>()
            if (bookmarks.contains(path)) statuses.add(ComicStatus.BOOKMARKED)
            if (playlists.contains(path)) statuses.add(ComicStatus.IN_PLAYLIST)
            path to statuses.toSet()
        }

        if (filteredComics.isEmpty()) {
            PlaylistUiState.Empty
        } else {
            PlaylistUiState.Success(
                comics = filteredComics,
                comicStatuses = statusMap,
                gridSize = gridSize
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlaylistUiState.Loading
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun selectCategory(id: Long?) {
        _selectedCategoryId.value = id
        clearSelection()
    }

    fun toggleSelection(path: String) {
        val current = _selectedPaths.value
        if (current.contains(path)) {
            _selectedPaths.value = current - path
        } else {
            _selectedPaths.value = current + path
        }
    }

    fun selectAll() {
        val currentState = uiState.value
        if (currentState is PlaylistUiState.Success) {
            _selectedPaths.value = currentState.comics.map { it.relativePath }.toSet()
        }
    }

    fun clearSelection() {
        _selectedPaths.value = emptySet()
    }

    fun removeSelected() {
        val paths = _selectedPaths.value.toList()
        val categoryId = _selectedCategoryId.value
        if (paths.isEmpty() || categoryId == null) return
        
        viewModelScope.launch {
            playlistRepository.removeComicsFromPlaylist(categoryId, paths)
            clearSelection()
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            val newId = playlistRepository.createPlaylist(name)
            if (_selectedCategoryId.value == null) {
                _selectedCategoryId.value = newId
            }
        }
    }

    fun renamePlaylist(id: Long, newName: String) {
        viewModelScope.launch {
            playlistRepository.renamePlaylist(id, newName)
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
    data class Success(
        val comics: List<Comic>,
        val comicStatuses: Map<String, Set<ComicStatus>>,
        val gridSize: Int
    ) : PlaylistUiState()
    data class Error(val message: String) : PlaylistUiState()
}
