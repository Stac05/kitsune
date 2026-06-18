package com.kitsune.app.ui.reader

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.core.StorageHelper
import com.kitsune.app.data.repository.ReaderRepository
import com.kitsune.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola logika layar Reader.
 */
class ReaderViewModel(
    private val comicRelativePath: String,
    private val chapterRelativePath: String,
    private val readerRepository: ReaderRepository,
    private val settingsRepository: SettingsRepository,
    private val storageHelper: StorageHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReaderUiState>(ReaderUiState.Loading)
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var _chapterUri: Uri? = null
    val chapterUri: Uri? get() = _chapterUri

    init {
        loadChapter()
    }

    private fun loadChapter() {
        viewModelScope.launch {
            try {
                val settings = settingsRepository.settings.first()
                val rootUriString = settings?.rootFolderUri

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
                    _uiState.value = ReaderUiState.Success(
                        pages = pages,
                        chapterName = chapterRelativePath.substringAfterLast('/')
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ReaderUiState.Error("Failed to load chapter: ${e.message}")
            }
        }
    }
}
