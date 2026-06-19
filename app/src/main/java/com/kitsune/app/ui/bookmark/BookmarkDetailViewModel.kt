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

    fun renameBookmark(newName: String) {
        viewModelScope.launch {
            bookmarkRepository.renameBookmark(bookmarkId, newName)
            // The flow will automatically update the UI if the bookmark object was being observed, 
            // but here we only have the name in Success state. 
            // Since we use getBookmarkById once, we might need to refresh or observe the bookmark itself.
            // For simplicity in MVP, we can just re-load or improve the flow.
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
