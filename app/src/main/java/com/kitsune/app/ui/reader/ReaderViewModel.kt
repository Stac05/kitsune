package com.kitsune.app.ui.reader

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.core.StorageHelper
import com.kitsune.app.data.repository.ReaderRepository
import com.kitsune.app.data.repository.ReadingProgressRepository
import com.kitsune.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola logika layar Reader.
 * Mendukung pembacaan progres dan pemantauan pengaturan mode baca.
 */
class ReaderViewModel(
    private val comicRelativePath: String,
    private val chapterRelativePath: String,
    private val readerRepository: ReaderRepository,
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ReadingProgressRepository,
    private val storageHelper: StorageHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var _chapterUri: Uri? = null
    val chapterUri: Uri? get() = _chapterUri

    // Melacak halaman saat ini untuk mendukung transisi antar mode baca
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    init {
        loadChapter()
        observeSettings()
    }

    private fun loadChapter() {
        viewModelScope.launch {
            try {
                val settings = settingsRepository.settings.first()
                val rootUriString = settings?.rootFolderUri
                val readingMode = settings?.readingMode ?: "Vertical"

                if (rootUriString.isNullOrEmpty()) {
                    _uiState.value = ReaderUiState.Error("Library not configured")
                    return@launch
                }

                val rootUri = rootUriString.toUri()
                val chapterDoc = storageHelper.findFileByRelativePath(rootUri, chapterRelativePath)

                if (chapterDoc == null || !chapterDoc.exists()) {
                    _uiState.value = ReaderUiState.Error("Chapter file not found")
                    return@launch
                }

                val uri = chapterDoc.uri
                _chapterUri = uri
                val pages = readerRepository.getPages(uri)
                
                if (pages.isEmpty()) {
                    _uiState.value = ReaderUiState.Empty
                } else {
                    // Ambil progres tersimpan untuk inisialisasi awal
                    val savedProgress = progressRepository.getProgressByComicSync(comicRelativePath)
                    val startPage = if (savedProgress != null && savedProgress.chapterRelativePath == chapterRelativePath) {
                        savedProgress.pageNumber.coerceIn(1, pages.size)
                    } else {
                        1
                    }
                    _currentPage.value = startPage

                    _uiState.value = ReaderUiState.Success(
                        pages = pages,
                        chapterName = chapterRelativePath.substringAfterLast('/'),
                        readingMode = readingMode
                    )
                    
                    saveProgress(startPage, pages.size)
                }
            } catch (e: Exception) {
                _uiState.value = ReaderUiState.Error("Failed to load chapter: ${e.message}")
            }
        }
    }

    /**
     * Memantau perubahan pengaturan mode baca secara real-time.
     */
    private fun observeSettings() {
        settingsRepository.settings
            .map { it?.readingMode ?: "Vertical" }
            .distinctUntilChanged()
            .onEach { mode ->
                val current = _uiState.value
                if (current is ReaderUiState.Success && current.readingMode != mode) {
                    _uiState.value = current.copy(readingMode = mode)
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Memperbarui progres membaca di database dan state lokal.
     */
    fun saveProgress(pageNumber: Int, totalPages: Int) {
        _currentPage.value = pageNumber
        viewModelScope.launch {
            progressRepository.saveProgress(
                comicRelativePath = comicRelativePath,
                chapterRelativePath = chapterRelativePath,
                pageNumber = pageNumber,
                totalPages = totalPages
            )
        }
    }
}
