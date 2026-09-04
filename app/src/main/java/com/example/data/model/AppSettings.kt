package com.example.data.model

data class AppSettings(
    val darkMode: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val scanAnimationEnabled: Boolean = true,
    val autoCopy: Boolean = false,
    val autoOpenUrl: Boolean = false,
    val defaultBrowser: String = "System Default",
    val language: String = "English"
)
