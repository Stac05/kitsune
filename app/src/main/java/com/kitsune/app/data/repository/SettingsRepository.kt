package com.kitsune.app.data.repository

import com.kitsune.app.database.dao.SettingsDao
import com.kitsune.app.database.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsDao: SettingsDao) {
    val settings: Flow<SettingsEntity?> = settingsDao.getSettings()

    suspend fun saveSettings(settings: SettingsEntity) {
        settingsDao.insertSettings(settings)
    }

    suspend fun updateRootFolderUri(uri: String) {
        settingsDao.updateRootFolderUri(uri)
    }
}
