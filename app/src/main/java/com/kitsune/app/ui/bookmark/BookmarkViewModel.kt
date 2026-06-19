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
 */
class BookmarkViewModel(
    private val bookmarkRepository: BookmarkRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarkUiState>(BookmarkUiState.Loading)
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

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
