package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.AppSettings
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

    val settings: StateFlow<AppSettings> = settingsRepository.settings

    fun setDarkMode(enabled: Boolean) = settingsRepository.updateDarkMode(enabled)
    fun setSound(enabled: Boolean) = settingsRepository.updateSound(enabled)
    fun setVibration(enabled: Boolean) = settingsRepository.updateVibration(enabled)
    fun setScanAnimation(enabled: Boolean) = settingsRepository.updateScanAnimation(enabled)
    fun setAutoCopy(enabled: Boolean) = settingsRepository.updateAutoCopy(enabled)
    fun setAutoOpenUrl(enabled: Boolean) = settingsRepository.updateAutoOpenUrl(enabled)
}
