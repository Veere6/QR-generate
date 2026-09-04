package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        return AppSettings(
            darkMode = prefs.getBoolean("dark_mode", true),
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            vibrationEnabled = prefs.getBoolean("vibration_enabled", true),
            scanAnimationEnabled = prefs.getBoolean("scan_animation", true),
            autoCopy = prefs.getBoolean("auto_copy", false),
            autoOpenUrl = prefs.getBoolean("auto_open_url", false),
            defaultBrowser = prefs.getString("default_browser", "System Default") ?: "System Default",
            language = prefs.getString("language", "English") ?: "English"
        )
    }

    fun updateDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("dark_mode", enabled).apply()
        _settings.value = _settings.value.copy(darkMode = enabled)
    }

    fun updateSound(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _settings.value = _settings.value.copy(soundEnabled = enabled)
    }

    fun updateVibration(enabled: Boolean) {
        prefs.edit().putBoolean("vibration_enabled", enabled).apply()
        _settings.value = _settings.value.copy(vibrationEnabled = enabled)
    }

    fun updateScanAnimation(enabled: Boolean) {
        prefs.edit().putBoolean("scan_animation", enabled).apply()
        _settings.value = _settings.value.copy(scanAnimationEnabled = enabled)
    }

    fun updateAutoCopy(enabled: Boolean) {
        prefs.edit().putBoolean("auto_copy", enabled).apply()
        _settings.value = _settings.value.copy(autoCopy = enabled)
    }

    fun updateAutoOpenUrl(enabled: Boolean) {
        prefs.edit().putBoolean("auto_open_url", enabled).apply()
        _settings.value = _settings.value.copy(autoOpenUrl = enabled)
    }
}
