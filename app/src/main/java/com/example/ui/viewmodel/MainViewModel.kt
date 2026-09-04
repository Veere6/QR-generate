package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ScanDatabase
import com.example.data.model.ParsedCategory
import com.example.data.model.ScanEntity
import com.example.data.model.ScanType
import com.example.data.repository.ScanRepository
import com.example.data.repository.SettingsRepository
import com.example.utils.ContentParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val scanDao = ScanDatabase.getDatabase(application).scanDao()
    val repository = ScanRepository(scanDao)
    val settingsRepository = SettingsRepository(application)

    val allScans: StateFlow<List<ScanEntity>> = repository.allScans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favorites: StateFlow<List<ScanEntity>> = repository.favorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val scanCount: StateFlow<Int> = repository.scanCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val favoriteCount: StateFlow<Int> = repository.favoriteCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    private val _currentScanResult = MutableStateFlow<ScanEntity?>(null)
    val currentScanResult: StateFlow<ScanEntity?> = _currentScanResult.asStateFlow()

    fun setCurrentResult(scanEntity: ScanEntity) {
        _currentScanResult.value = scanEntity
    }

    fun saveScanResult(
        content: String,
        scanType: ScanType,
        format: com.example.data.model.BarcodeFormatType
    ) {
        viewModelScope.launch {
            val category = ContentParser.parseCategory(content)
            val title = ContentParser.parseTitle(content, category)

            val scanEntity = ScanEntity(
                content = content,
                title = title,
                scanType = scanType,
                format = format,
                category = category
            )

            val newId = repository.insertScan(scanEntity)
            val savedEntity = scanEntity.copy(id = newId)
            _currentScanResult.value = savedEntity
        }
    }

    fun toggleFavorite(scanEntity: ScanEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(scanEntity)
            if (_currentScanResult.value?.id == scanEntity.id) {
                _currentScanResult.value = _currentScanResult.value?.copy(isFavorite = !scanEntity.isFavorite)
            }
        }
    }

    fun processScanWithSettings(
        context: android.content.Context,
        content: String,
        scanType: ScanType,
        format: com.example.data.model.BarcodeFormatType,
        settings: com.example.data.model.AppSettings
    ) {
        saveScanResult(content, scanType, format)

        if (settings.autoCopy) {
            try {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Scanned Code", content)
                clipboard.setPrimaryClip(clip)
                android.widget.Toast.makeText(context, "Copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (settings.autoOpenUrl && (content.startsWith("http://", ignoreCase = true) || content.startsWith("https://", ignoreCase = true))) {
            try {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(content))
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
            _currentScanResult.value = null
        }
    }

    fun deleteScan(scanEntity: ScanEntity) {
        viewModelScope.launch {
            repository.deleteScan(scanEntity)
            if (_currentScanResult.value?.id == scanEntity.id) {
                _currentScanResult.value = null
            }
        }
    }
}
