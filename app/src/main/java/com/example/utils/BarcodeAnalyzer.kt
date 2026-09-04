package com.example.utils

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.data.model.BarcodeFormatType
import com.example.data.model.ScanType
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val targetScanType: ScanType? = null,
    private val onBarcodeDetected: (rawContent: String, format: BarcodeFormatType, type: ScanType) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)
    private var isProcessing = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || isProcessing) {
            imageProxy.close()
            return
        }

        isProcessing = true
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcode = barcodes.first()
                    val rawValue = barcode.rawValue ?: barcode.displayValue ?: ""
                    if (rawValue.isNotBlank()) {
                        val format = mapMlKitFormat(barcode.format)
                        val scanType = if (barcode.format == Barcode.FORMAT_QR_CODE) ScanType.QR_CODE else ScanType.BARCODE

                        if (targetScanType == null || targetScanType == scanType) {
                            onBarcodeDetected(rawValue, format, scanType)
                        }
                    }
                }
            }
            .addOnCompleteListener {
                isProcessing = false
                imageProxy.close()
            }
    }

    companion object {
        fun mapMlKitFormat(mlKitFormat: Int): BarcodeFormatType {
            return when (mlKitFormat) {
                Barcode.FORMAT_EAN_13 -> BarcodeFormatType.EAN_13
                Barcode.FORMAT_EAN_8 -> BarcodeFormatType.EAN_8
                Barcode.FORMAT_UPC_A -> BarcodeFormatType.UPC_A
                Barcode.FORMAT_UPC_E -> BarcodeFormatType.UPC_E
                Barcode.FORMAT_CODE_39 -> BarcodeFormatType.CODE_39
                Barcode.FORMAT_CODE_128 -> BarcodeFormatType.CODE_128
                Barcode.FORMAT_ITF -> BarcodeFormatType.ITF
                Barcode.FORMAT_PDF417 -> BarcodeFormatType.PDF417
                Barcode.FORMAT_AZTEC -> BarcodeFormatType.AZTEC
                Barcode.FORMAT_DATA_MATRIX -> BarcodeFormatType.DATA_MATRIX
                Barcode.FORMAT_QR_CODE -> BarcodeFormatType.QR_CODE
                else -> BarcodeFormatType.UNKNOWN
            }
        }
    }
}
