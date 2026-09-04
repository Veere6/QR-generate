package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppSettings
import com.example.data.model.BarcodeFormatType
import com.example.data.model.ScanType
import com.example.data.repository.SettingsRepository
import com.example.utils.BarcodeAnalyzer
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    val settings: StateFlow<AppSettings> = settingsRepository.settings

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _continuousMode = MutableStateFlow(false)
    val continuousMode: StateFlow<Boolean> = _continuousMode.asStateFlow()

    private val _scanCount = MutableStateFlow(0)
    val scanCount: StateFlow<Int> = _scanCount.asStateFlow()

    fun toggleFlash() {
        _isFlashOn.value = !_isFlashOn.value
    }

    fun toggleCamera() {
        _isFrontCamera.value = !_isFrontCamera.value
    }

    fun toggleVibration() {
        settingsRepository.updateVibration(!settings.value.vibrationEnabled)
    }

    fun toggleSound() {
        settingsRepository.updateSound(!settings.value.soundEnabled)
    }

    fun toggleContinuousMode() {
        _continuousMode.value = !_continuousMode.value
    }

    fun incrementScanCount() {
        _scanCount.value += 1
    }

    fun scanImageFromGallery(
        context: Context,
        uri: Uri,
        onSuccess: (rawContent: String, format: BarcodeFormatType, scanType: ScanType) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val image = InputImage.fromFilePath(context, uri)
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
                val scanner = BarcodeScanning.getClient(options)

                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            val barcode = barcodes.first()
                            val rawValue = barcode.rawValue ?: barcode.displayValue ?: ""
                            if (rawValue.isNotEmpty()) {
                                val format = BarcodeAnalyzer.mapMlKitFormat(barcode.format)
                                val scanType = if (barcode.format == Barcode.FORMAT_QR_CODE) ScanType.QR_CODE else ScanType.BARCODE
                                onSuccess(rawValue, format, scanType)
                            } else {
                                onError("No readable code found in image")
                            }
                        } else {
                            onError("No QR or Barcode detected in this image")
                        }
                    }
                    .addOnFailureListener {
                        onError("Failed to analyze image: ${it.localizedMessage}")
                    }
            } catch (e: Exception) {
                onError("Could not load image file")
            }
        }
    }
}
