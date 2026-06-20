package com.kitsune.app.ui.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.data.repository.ReadingProgressRepository
import com.kitsune.app.domain.model.LastReadComic
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface LocalUiState {
    object Loading : LocalUiState
    data class Success(val lastRead: LastReadComic?) : LocalUiState
    data class Error(val message: String) : LocalUiState
}

class LocalViewModel(
    private val progressRepository: ReadingProgressRepository
) : ViewModel() {

    val uiState: StateFlow<LocalUiState> = progressRepository.getLatestReadComic()
        .map { lastRead ->
            LocalUiState.Success(lastRead)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocalUiState.Loading
        )
}
