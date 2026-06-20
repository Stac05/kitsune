package com.kitsune.app.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.data.repository.BookmarkRepository
import com.kitsune.app.data.repository.BookmarkWithCount
import com.kitsune.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola daftar Bookmark.
 * Mendukung Selection Mode untuk pengelolaan massal.
 */
class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarkUiState>(BookmarkUiState.Loading)
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    // Selection Mode State
    private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

    val selectionMode: StateFlow<Boolean> = _selectedIds
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            combine(
                bookmarkRepository.getAllBookmarksWithCount(),
                settingsRepository.settings.map { it?.gridSize ?: 3 }
            ) { bookmarks, gridSize ->
                if (bookmarks.isEmpty()) {
                    BookmarkUiState.Empty
                } else {
                    BookmarkUiState.Success(bookmarks, gridSize)
                }
            }.catch { e ->
                _uiState.value = BookmarkUiState.Error(e.message ?: "Unknown error")
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
        if (state is BookmarkUiState.Success) {
            _selectedIds.value = state.bookmarks.map { it.bookmark.id }.toSet()
        }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelected() {
        val ids = _selectedIds.value.toList()
        if (ids.isEmpty()) return
        
        viewModelScope.launch {
            bookmarkRepository.deleteBookmarks(ids)
            clearSelection()
        }
    }

    fun createBookmark(name: String) {
        viewModelScope.launch {
            bookmarkRepository.createBookmark(name)
        }
    }

    fun deleteBookmark(id: Long) {
        viewModelScope.launch {
            bookmarkRepository.deleteBookmark(id)
        }
    }
}

sealed class BookmarkUiState {
    data object Loading : BookmarkUiState()
    data object Empty : BookmarkUiState()
    data class Success(val bookmarks: List<BookmarkWithCount>, val gridSize: Int) : BookmarkUiState()
    data class Error(val message: String) : BookmarkUiState()
}
