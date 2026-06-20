package com.kitsune.app.data.repository

import com.kitsune.app.database.dao.SettingsDao
import com.kitsune.app.database.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SettingsRepository(private val settingsDao: SettingsDao) {
    val settings: Flow<SettingsEntity?> = settingsDao.getSettings()

    suspend fun saveSettings(settings: SettingsEntity) {
        settingsDao.insertSettings(settings)
    }

    suspend fun updateRootFolderUri(uri: String) {
        val currentSettings = settings.first() ?: SettingsEntity()
        settingsDao.insertSettings(currentSettings.copy(rootFolderUri = uri))
    }

    suspend fun updateReadingMode(mode: String) {
        val currentSettings = settings.first() ?: SettingsEntity()
        settingsDao.insertSettings(currentSettings.copy(readingMode = mode))
    }

    suspend fun updateGridSize(size: Int) {
        val currentSettings = settings.first() ?: SettingsEntity()
        settingsDao.insertSettings(currentSettings.copy(gridSize = size))
    }

    suspend fun updateDarkMode(enabled: Boolean) {
        val currentSettings = settings.first() ?: SettingsEntity()
        settingsDao.insertSettings(currentSettings.copy(darkMode = enabled))
    }

    suspend fun updateOledBlack(enabled: Boolean) {
        val currentSettings = settings.first() ?: SettingsEntity()
        settingsDao.insertSettings(currentSettings.copy(oledBlack = enabled))
    }
}
