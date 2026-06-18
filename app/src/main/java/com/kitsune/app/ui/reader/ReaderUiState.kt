package com.kitsune.app.ui.reader

import com.kitsune.app.domain.model.Page

/**
 * State UI untuk layar Reader.
 */
sealed class ReaderUiState {
    data object Loading : ReaderUiState()
    data class Success(
        val pages: List<Page>,
        val chapterName: String
    ) : ReaderUiState()
    data class Error(val message: String) : ReaderUiState()
    data object Empty : ReaderUiState()
}
