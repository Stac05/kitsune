package com.kitsune.app.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kitsune.app.core.StorageHelper
import com.kitsune.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val settingsRepository: SettingsRepository,
    private val storageHelper: StorageHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        checkRootFolder()
    }

    private fun checkRootFolder() {
        viewModelScope.launch {
            val settings = settingsRepository.settings.first()
            val uriString = settings?.rootFolderUri
            
            if (storageHelper.isUriPermissionValid(uriString)) {
                _uiState.value = SplashUiState.Authenticated
            } else {
                _uiState.value = SplashUiState.NeedsSetup
            }
        }
    }

    fun onRootFolderSelected(uriString: String) {
        viewModelScope.launch {
            if (storageHelper.validateAndCreateStructure(uriString)) {
                settingsRepository.updateRootFolderUri(uriString)
                _uiState.value = SplashUiState.Authenticated
            } else {
                _uiState.value = SplashUiState.Error("Invalid folder structure")
            }
        }
    }
}

sealed class SplashUiState {
    object Loading : SplashUiState()
    object NeedsSetup : SplashUiState()
    object Authenticated : SplashUiState()
    data class Error(val message: String) : SplashUiState()
}
