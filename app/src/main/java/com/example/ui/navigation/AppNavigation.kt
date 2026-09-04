package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.BarcodeScannerScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QrGeneratorScreen
import com.example.ui.screens.QrScannerScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.viewmodel.GeneratorViewModel
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScannerViewModel
import com.example.ui.viewmodel.SettingsViewModel

object Screen {
    const val HOME = "home"
    const val QR_SCANNER = "qr_scanner"
    const val BARCODE_SCANNER = "barcode_scanner"
    const val RESULT = "result"
    const val GENERATOR = "generator"
    const val HISTORY = "history"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val SEARCH = "search"
}

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    scannerViewModel: ScannerViewModel,
    generatorViewModel: GeneratorViewModel,
    historyViewModel: HistoryViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var generatorInitialText by remember { mutableStateOf("") }

    val showBottomBar = currentRoute in listOf(
        Screen.HOME,
        Screen.HISTORY,
        Screen.FAVORITES,
        Screen.SETTINGS
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.HOME
            ) {
                composable(Screen.HOME) {
                    HomeScreen(
                        mainViewModel = mainViewModel,
                        onNavigateToQrScanner = { navController.navigate(Screen.QR_SCANNER) },
                        onNavigateToBarcodeScanner = { navController.navigate(Screen.BARCODE_SCANNER) },
                        onNavigateToGenerator = {
                            generatorInitialText = ""
                            navController.navigate(Screen.GENERATOR)
                        },
                        onNavigateToHistory = { navController.navigate(Screen.HISTORY) },
                        onNavigateToSearch = { navController.navigate(Screen.SEARCH) },
                        onNavigateToSettings = { navController.navigate(Screen.SETTINGS) },
                        onSelectScanResult = { navController.navigate(Screen.RESULT) }
                    )
                }

                composable(Screen.QR_SCANNER) {
                    QrScannerScreen(
                        mainViewModel = mainViewModel,
                        scannerViewModel = scannerViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToResult = { navController.navigate(Screen.RESULT) },
                        onNavigateToHistory = { navController.navigate(Screen.HISTORY) }
                    )
                }

                composable(Screen.BARCODE_SCANNER) {
                    BarcodeScannerScreen(
                        mainViewModel = mainViewModel,
                        scannerViewModel = scannerViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToResult = { navController.navigate(Screen.RESULT) },
                        onNavigateToHistory = { navController.navigate(Screen.HISTORY) }
                    )
                }

                composable(Screen.RESULT) {
                    ResultScreen(
                        mainViewModel = mainViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToGeneratorWithText = { text ->
                            generatorInitialText = text
                            navController.navigate(Screen.GENERATOR)
                        }
                    )
                }

                composable(Screen.GENERATOR) {
                    QrGeneratorScreen(
                        generatorViewModel = generatorViewModel,
                        initialText = generatorInitialText,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.HISTORY) {
                    HistoryScreen(
                        mainViewModel = mainViewModel,
                        historyViewModel = historyViewModel,
                        onSelectScanResult = { navController.navigate(Screen.RESULT) }
                    )
                }

                composable(Screen.FAVORITES) {
                    FavoritesScreen(
                        mainViewModel = mainViewModel,
                        onSelectScanResult = { navController.navigate(Screen.RESULT) }
                    )
                }

                composable(Screen.SETTINGS) {
                    SettingsScreen(
                        mainViewModel = mainViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }

                composable(Screen.SEARCH) {
                    SearchScreen(
                        mainViewModel = mainViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onSelectScanResult = { navController.navigate(Screen.RESULT) }
                    )
                }
            }
        }
    }
}
