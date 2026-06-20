package com.kitsune.app.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.data.repository.BookmarkRepository
import com.kitsune.app.data.repository.ScannerRepository
import com.kitsune.app.data.repository.SettingsRepository
import com.kitsune.app.domain.model.Comic
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookmarkDetailViewModel(
    private val bookmarkId: Long,
    private val bookmarkRepository: BookmarkRepository,
    private val scannerRepository: ScannerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarkDetailUiState>(BookmarkDetailUiState.Loading)
    val uiState: StateFlow<BookmarkDetailUiState> = _uiState.asStateFlow()

    // Selection Mode State
    private val _selectedPaths = MutableStateFlow<Set<String>>(emptySet())
    val selectedPaths: StateFlow<Set<String>> = _selectedPaths.asStateFlow()

    val selectionMode: StateFlow<Boolean> = _selectedPaths
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadBookmarkDetail()
    }

    private fun loadBookmarkDetail() {
        viewModelScope.launch {
            val bookmark = bookmarkRepository.getBookmarkById(bookmarkId)
            if (bookmark == null) {
                _uiState.value = BookmarkDetailUiState.Error("Bookmark not found")
                return@launch
            }

            combine(
                bookmarkRepository.getComicsInBookmark(bookmarkId),
                scannerRepository.allComics,
                settingsRepository.settings.map { it?.gridSize ?: 3 }
            ) { bookmarkedPaths, allComics, gridSize ->
                val comics = allComics.filter { it.relativePath in bookmarkedPaths }
                BookmarkDetailUiState.Success(
                    bookmarkName = bookmark.name,
                    comics = comics,
                    gridSize = gridSize
                )
            }.catch { e ->
                _uiState.value = BookmarkDetailUiState.Error(e.message ?: "Unknown error")
            }.collect { state ->
                _uiState.value = state
            }
        }
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
        val state = _uiState.value
        if (state is BookmarkDetailUiState.Success) {
            _selectedPaths.value = state.comics.map { it.relativePath }.toSet()
        }
    }

    fun clearSelection() {
        _selectedPaths.value = emptySet()
    }

    fun removeSelected() {
        val paths = _selectedPaths.value.toList()
        if (paths.isEmpty()) return
        
        viewModelScope.launch {
            bookmarkRepository.removeComicsFromBookmark(bookmarkId, paths)
            clearSelection()
        }
    }

    fun renameBookmark(newName: String) {
        viewModelScope.launch {
            bookmarkRepository.renameBookmark(bookmarkId, newName)
        }
    }

    fun removeComic(comicPath: String) {
        viewModelScope.launch {
            bookmarkRepository.removeComicFromBookmark(bookmarkId, comicPath)
        }
    }

    fun deleteBookmark() {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(bookmarkId)
        }
    }
}

sealed class BookmarkDetailUiState {
    data object Loading : BookmarkDetailUiState()
    data class Success(
        val bookmarkName: String,
        val comics: List<Comic>,
        val gridSize: Int
    ) : BookmarkDetailUiState()
    data class Error(val message: String) : BookmarkDetailUiState()
}
