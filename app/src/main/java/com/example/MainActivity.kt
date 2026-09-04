package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.QrScannerTheme
import com.example.ui.viewmodel.GeneratorViewModel
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScannerViewModel
import com.example.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val scannerViewModel: ScannerViewModel by viewModels()
    private val generatorViewModel: GeneratorViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            QrScannerTheme(darkTheme = settings.darkMode) {
                AppNavigation(
                    mainViewModel = mainViewModel,
                    scannerViewModel = scannerViewModel,
                    generatorViewModel = generatorViewModel,
                    historyViewModel = historyViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
