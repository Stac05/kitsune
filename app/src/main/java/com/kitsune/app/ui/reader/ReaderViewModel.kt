package com.kitsune.app.ui.reader

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.core.StorageHelper
import com.kitsune.app.data.repository.ReaderRepository
import com.kitsune.app.data.repository.ReadingProgressRepository
import com.kitsune.app.data.repository.ScannerRepository
import com.kitsune.app.data.repository.SettingsRepository
import com.kitsune.app.domain.model.Chapter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola logika layar Reader.
 * Mendukung pembacaan progres, pemantauan pengaturan mode baca, dan navigasi antar chapter.
 */
class ReaderViewModel(
    private val comicRelativePath: String,
    private var currentChapterPath: String,
    private val readerRepository: ReaderRepository,
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ReadingProgressRepository,
    private val scannerRepository: ScannerRepository,
    private val storageHelper: StorageHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var _chapterUri = MutableStateFlow<Uri?>(null)
    val chapterUri: StateFlow<Uri?> = _chapterUri.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private var chapters: List<Chapter> = emptyList()
    private var currentChapterIndex: Int = -1

    init {
        loadChapter(currentChapterPath)
        observeSettings()
    }

    private fun loadChapter(chapterPath: String) {
        viewModelScope.launch {
            _uiState.value = ReaderUiState.Loading
            try {
                val settings = settingsRepository.settings.first()
                val rootUriString = settings?.rootFolderUri
                val readingMode = settings?.readingMode ?: "Vertical"

                if (rootUriString.isNullOrEmpty()) {
                    _uiState.value = ReaderUiState.Error("Library not configured")
                    return@launch
                }

                val rootUri = rootUriString.toUri()
                
                // Load chapters list if not already loaded
                if (chapters.isEmpty()) {
                    chapters = scannerRepository.getChapters(rootUri, comicRelativePath)
                }
                currentChapterIndex = chapters.indexOfFirst { it.relativePath == chapterPath }

                val chapterDoc = storageHelper.findFileByRelativePath(rootUri, chapterPath)

                if (chapterDoc == null || !chapterDoc.exists()) {
                    _uiState.value = ReaderUiState.Error("Chapter file not found")
                    return@launch
                }

                val uri = chapterDoc.uri
                _chapterUri.value = uri
                val pages = readerRepository.getPages(uri)
                
                if (pages.isEmpty()) {
                    _uiState.value = ReaderUiState.Empty
                } else {
                    currentChapterPath = chapterPath
                    val savedProgress = progressRepository.getProgressByComicSync(comicRelativePath)
                    val startPage = if (savedProgress != null && savedProgress.chapterRelativePath == chapterPath) {
                        savedProgress.pageNumber.coerceIn(1, pages.size)
                    } else {
                        1
                    }
                    _currentPage.value = startPage

                    _uiState.value = ReaderUiState.Success(
                        pages = pages,
                        chapterName = chapterPath.substringAfterLast('/'),
                        readingMode = readingMode
                    )
                    
                    saveProgress(startPage, pages.size)
                }
            } catch (e: Exception) {
                _uiState.value = ReaderUiState.Error("Failed to load chapter: ${e.message}")
            }
        }
    }

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

    fun saveProgress(pageNumber: Int, totalPages: Int) {
        _currentPage.value = pageNumber
        viewModelScope.launch {
            progressRepository.saveProgress(
                comicRelativePath = comicRelativePath,
                chapterRelativePath = currentChapterPath,
                pageNumber = pageNumber,
                totalPages = totalPages
            )
        }
    }

    fun navigateToNextChapter() {
        if (currentChapterIndex < chapters.size - 1) {
            val nextChapter = chapters[currentChapterIndex + 1]
            loadChapter(nextChapter.relativePath)
        }
    }

    fun navigateToPreviousChapter() {
        if (currentChapterIndex > 0) {
            val prevChapter = chapters[currentChapterIndex - 1]
            loadChapter(prevChapter.relativePath)
        }
    }

    fun hasNextChapter() = currentChapterIndex < chapters.size - 1
    fun hasPreviousChapter() = currentChapterIndex > 0
    
    fun jumpToPage(pageNumber: Int) {
        val current = _uiState.value
        if (current is ReaderUiState.Success) {
            val validatedPage = pageNumber.coerceIn(1, current.pages.size)
            _currentPage.value = validatedPage
            saveProgress(validatedPage, current.pages.size)
        }
    }
}
