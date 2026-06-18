package com.kitsune.app.ui.comicdetail

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * Memuat metadata komik, daftar chapter, dan progres membaca secara lazy.
 */
class ComicDetailViewModel(
    private val comicRelativePath: String,
    private val scannerRepository: ScannerRepository,
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ReadingProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ComicDetailUiState>(ComicDetailUiState.Loading)
    val uiState: StateFlow<ComicDetailUiState> = _uiState.asStateFlow()

    init {
        loadComicDetail()
    }

    private fun loadComicDetail() {
        viewModelScope.launch {
            try {
                // 1. Ambil data komik dari repository
                val comic = scannerRepository.getComicByPath(comicRelativePath)
                if (comic == null) {
                    _uiState.value = ComicDetailUiState.Error("Comic not found")
                    return@launch
                }

                // 2. Ambil root URI untuk memindai chapter
                val settings = settingsRepository.settings.first()
                val rootUriString = settings?.rootFolderUri

                if (rootUriString.isNullOrEmpty()) {
                    _uiState.value = ComicDetailUiState.Error("Library not configured")
                    return@launch
                }

                // 3. Scan chapter di dalam folder komik (Lazy Loading)
                val chapters = scannerRepository.getChapters(rootUriString.toUri(), comicRelativePath)

                // 4. Ambil progres membaca terbaru secara real-time
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
