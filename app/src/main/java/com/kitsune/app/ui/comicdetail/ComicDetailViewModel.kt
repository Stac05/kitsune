package com.kitsune.app.ui.comicdetail

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.data.repository.BookmarkRepository
import com.kitsune.app.data.repository.BookmarkWithCount
import com.kitsune.app.data.repository.PlaylistRepository
import com.kitsune.app.data.repository.PlaylistWithCount
import com.kitsune.app.data.repository.ReadingProgressRepository
import com.kitsune.app.data.repository.ScannerRepository
import com.kitsune.app.data.repository.SettingsRepository
import com.kitsune.app.database.entity.ReadingProgressEntity
import com.kitsune.app.domain.model.Chapter
import com.kitsune.app.domain.model.Comic
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola data detail sebuah komik.
 * Memuat metadata komik, daftar chapter, progres membaca, status bookmark, dan playlist secara lazy.
 */
class ComicDetailViewModel(
    private val comicRelativePath: String,
    private val scannerRepository: ScannerRepository,
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ReadingProgressRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ComicDetailUiState>(ComicDetailUiState.Loading)
    val uiState: StateFlow<ComicDetailUiState> = _uiState.asStateFlow()

    private val _availableBookmarks = MutableStateFlow<List<BookmarkWithCount>>(emptyList())
    val availableBookmarks: StateFlow<List<BookmarkWithCount>> = _availableBookmarks.asStateFlow()

    private val _availablePlaylists = MutableStateFlow<List<PlaylistWithCount>>(emptyList())
    val availablePlaylists: StateFlow<List<PlaylistWithCount>> = _availablePlaylists.asStateFlow()

    init {
        loadComicDetail()
        loadAvailableCollections()
    }

    private fun loadComicDetail() {
        viewModelScope.launch {
            try {
                val comic = scannerRepository.getComicByPath(comicRelativePath)
                if (comic == null) {
                    _uiState.value = ComicDetailUiState.Error("Comic not found")
                    return@launch
                }

                val settings = settingsRepository.settings.first()
                val rootUriString = settings?.rootFolderUri

                if (rootUriString.isNullOrEmpty()) {
                    _uiState.value = ComicDetailUiState.Error("Library not configured")
                    return@launch
                }

                val chapters = scannerRepository.getChapters(rootUriString.toUri(), comicRelativePath)

                progressRepository.getProgressByComic(comicRelativePath).collect { progress ->
                    _uiState.value = ComicDetailUiState.Success(
                        comic = comic,
                        chapters = chapters,
                        progress = progress
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ComicDetailUiState.Error("Failed to load details: ${e.message}")
            }
        }
    }

    private fun loadAvailableCollections() {
        viewModelScope.launch {
            bookmarkRepository.getAllBookmarksWithCount().onEach { bookmarks ->
                _availableBookmarks.value = bookmarks
            }.launchIn(this)

            playlistRepository.getAllPlaylistsWithCount().onEach { playlists ->
                _availablePlaylists.value = playlists
            }.launchIn(this)
        }
    }

    fun addComicToBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            bookmarkRepository.addComicToBookmark(bookmarkId, comicRelativePath)
        }
    }

    fun addComicToPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.addComicToPlaylist(playlistId, comicRelativePath)
        }
    }
}

/**
 * Representasi State UI untuk layar Detail Komik.
 */
sealed class ComicDetailUiState {
    data object Loading : ComicDetailUiState()
    data class Success(
        val comic: Comic,
        val chapters: List<Chapter>,
        val progress: ReadingProgressEntity?
    ) : ComicDetailUiState()
    data class Error(val message: String) : ComicDetailUiState()
}
