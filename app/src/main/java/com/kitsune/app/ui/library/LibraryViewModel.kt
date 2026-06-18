package com.kitsune.app.ui.library

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.data.repository.ScannerRepository
import com.kitsune.app.data.repository.SettingsRepository
import com.kitsune.app.domain.model.Comic
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola data pada layar Library Komik.
 * Menangani sinkronisasi antara Database dan Filesystem (Incremental Scan)
 * serta logika pencarian dan filtering.
 */
class LibraryViewModel(
    private val scannerRepository: ScannerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<LibraryUiState> = combine(
        scannerRepository.allComics,
        settingsRepository.settings,
        _isRefreshing,
        _errorMessage,
        _searchQuery
    ) { comics, settings, refreshing, error, query ->
        val gridSize = settings?.gridSize ?: 3
        
        // Strategi Filtering: Real-time case-insensitive search
        // Struktur ini memudahkan penambahan filter author/genre di masa depan
        val filteredComics = if (query.isBlank()) {
            comics
        } else {
            comics.filter { comic ->
                comic.title.contains(query, ignoreCase = true)
                // TODO: Tambahkan pencarian berdasarkan author/genre jika data tersedia di masa depan
            }
        }

        when {
            error != null -> LibraryUiState.Error(error)
            refreshing && filteredComics.isEmpty() && query.isBlank() -> LibraryUiState.Loading
            filteredComics.isEmpty() -> LibraryUiState.Empty
            else -> LibraryUiState.Success(
                comics = filteredComics,
                isRefreshing = refreshing,
                gridSize = gridSize
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState.Loading
    )

    init {
        // Melakukan scan otomatis saat library dibuka untuk memastikan sinkronisasi
        refreshLibrary()
    }

    /**
     * Memperbarui query pencarian secara real-time.
     */
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    /**
     * Memicu pemindaian ulang (Incremental Scan) pada folder root yang dikonfigurasi.
     */
    fun refreshLibrary() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            try {
                val settings = settingsRepository.settings.first()
                val rootUriString = settings?.rootFolderUri
                
                if (!rootUriString.isNullOrEmpty()) {
                    scannerRepository.performIncrementalScan(rootUriString.toUri())
                } else {
                    _errorMessage.value = "Root folder not configured"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to scan library: ${e.message}"
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

/**
 * Representasi State UI untuk layar Library.
 */
sealed class LibraryUiState {
    data object Loading : LibraryUiState()
    data object Empty : LibraryUiState()
    data class Success(
        val comics: List<Comic>, 
        val isRefreshing: Boolean,
        val gridSize: Int
    ) : LibraryUiState()
    data class Error(val message: String) : LibraryUiState()
}
