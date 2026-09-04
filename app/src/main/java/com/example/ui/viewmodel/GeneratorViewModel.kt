package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.ui.theme.PrimaryViolet
import com.example.utils.QrBodyShape
import com.example.utils.QrCodeGenerator
import com.example.utils.QrEyeShape
import com.example.utils.QrLogoType
import com.example.utils.QrStickerFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class GeneratorTab {
    URL, TEXT, PHONE, SMS, EMAIL, WIFI, CONTACT, LOCATION, UPI, EVENT
}

class GeneratorViewModel : ViewModel() {

    private val _selectedTab = MutableStateFlow(GeneratorTab.URL)
    val selectedTab: StateFlow<GeneratorTab> = _selectedTab.asStateFlow()

    // Input States
    val urlInput = MutableStateFlow("https://")
    val textInput = MutableStateFlow("")
    val phoneInput = MutableStateFlow("")
    val smsPhoneInput = MutableStateFlow("")
    val smsMessageInput = MutableStateFlow("")
    val emailInput = MutableStateFlow("")
    val emailSubjectInput = MutableStateFlow("")
    val emailBodyInput = MutableStateFlow("")

    val wifiSsidInput = MutableStateFlow("")
    val wifiPasswordInput = MutableStateFlow("")
    val wifiSecurityInput = MutableStateFlow("WPA") // WPA, WEP, nopass

    val contactNameInput = MutableStateFlow("")
    val contactPhoneInput = MutableStateFlow("")
    val contactEmailInput = MutableStateFlow("")
    val contactOrgInput = MutableStateFlow("")

    val latInput = MutableStateFlow("")
    val lngInput = MutableStateFlow("")

    val upiVpaInput = MutableStateFlow("")
    val upiNameInput = MutableStateFlow("")
    val upiAmountInput = MutableStateFlow("")

    val eventTitleInput = MutableStateFlow("")
    val eventLocationInput = MutableStateFlow("")

    // Style & Design States
    val foregroundColor = MutableStateFlow(PrimaryViolet)
    val backgroundColor = MutableStateFlow(Color.White)
    val selectedSticker = MutableStateFlow(QrStickerFrame.NONE)
    val bodyShape = MutableStateFlow(QrBodyShape.SQUARE)
    val eyeShape = MutableStateFlow(QrEyeShape.SQUARE)
    val selectedLogo = MutableStateFlow(QrLogoType.NONE)
    val customLogoBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedPreset = MutableStateFlow("Classic")

    private val _generatedBitmap = MutableStateFlow<Bitmap?>(null)
    val generatedBitmap: StateFlow<Bitmap?> = _generatedBitmap.asStateFlow()

    fun selectTab(tab: GeneratorTab) {
        _selectedTab.value = tab
        generateQr()
    }

    fun setSticker(sticker: QrStickerFrame) {
        selectedSticker.value = sticker
        generateQr()
    }

    fun setBodyShape(shape: QrBodyShape) {
        bodyShape.value = shape
        generateQr()
    }

    fun setEyeShape(shape: QrEyeShape) {
        eyeShape.value = shape
        generateQr()
    }

    fun setLogo(logo: QrLogoType) {
        selectedLogo.value = logo
        generateQr()
    }

    fun setCustomLogo(bitmap: Bitmap?) {
        customLogoBitmap.value = bitmap
        selectedLogo.value = QrLogoType.CUSTOM
        generateQr()
    }

    fun applyPreset(presetName: String) {
        selectedPreset.value = presetName
        when (presetName) {
            "Classic" -> {
                foregroundColor.value = PrimaryViolet
                backgroundColor.value = Color.White
                bodyShape.value = QrBodyShape.SQUARE
                eyeShape.value = QrEyeShape.SQUARE
            }
            "Neon" -> {
                foregroundColor.value = Color(0xFF00E5FF)
                backgroundColor.value = Color(0xFF0D111A)
                bodyShape.value = QrBodyShape.DOTS
                eyeShape.value = QrEyeShape.CIRCLE
            }
            "Emerald" -> {
                foregroundColor.value = Color(0xFF00E676)
                backgroundColor.value = Color(0xFFF4F6F8)
                bodyShape.value = QrBodyShape.ROUNDED
                eyeShape.value = QrEyeShape.ROUNDED
            }
            "Sunset" -> {
                foregroundColor.value = Color(0xFFFF6D00)
                backgroundColor.value = Color(0xFFFFF8E1)
                bodyShape.value = QrBodyShape.STARS
                eyeShape.value = QrEyeShape.LEAF
            }
            "Cyberpunk" -> {
                foregroundColor.value = Color(0xFFFF0055)
                backgroundColor.value = Color(0xFF121212)
                bodyShape.value = QrBodyShape.TRIANGLES
                eyeShape.value = QrEyeShape.SQUARE
            }
            "Monochrome" -> {
                foregroundColor.value = Color.Black
                backgroundColor.value = Color.White
                bodyShape.value = QrBodyShape.SQUARE
                eyeShape.value = QrEyeShape.SQUARE
            }
        }
        generateQr()
    }

    fun buildContentString(): String {
        return when (_selectedTab.value) {
            GeneratorTab.URL -> urlInput.value.trim()
            GeneratorTab.TEXT -> textInput.value.trim()
            GeneratorTab.PHONE -> "tel:${phoneInput.value.trim()}"
            GeneratorTab.SMS -> "smsto:${smsPhoneInput.value.trim()}:${smsMessageInput.value.trim()}"
            GeneratorTab.EMAIL -> "mailto:${emailInput.value.trim()}?subject=${UriEncode(emailSubjectInput.value)}&body=${UriEncode(emailBodyInput.value)}"
            GeneratorTab.WIFI -> "WIFI:S:${wifiSsidInput.value.trim()};T:${wifiSecurityInput.value};P:${wifiPasswordInput.value.trim()};;"
            GeneratorTab.CONTACT -> """
                BEGIN:VCARD
                VERSION:3.0
                N:${contactNameInput.value.trim()}
                FN:${contactNameInput.value.trim()}
                ORG:${contactOrgInput.value.trim()}
                TEL:${contactPhoneInput.value.trim()}
                EMAIL:${contactEmailInput.value.trim()}
                END:VCARD
            """.trimIndent()
            GeneratorTab.LOCATION -> "geo:${latInput.value.trim()},${lngInput.value.trim()}"
            GeneratorTab.UPI -> "upi://pay?pa=${upiVpaInput.value.trim()}&pn=${UriEncode(upiNameInput.value.trim())}&am=${upiAmountInput.value.trim()}&cu=INR"
            GeneratorTab.EVENT -> """
                BEGIN:VEVENT
                SUMMARY:${eventTitleInput.value.trim()}
                LOCATION:${eventLocationInput.value.trim()}
                END:VEVENT
            """.trimIndent()
        }
    }

    fun generateQr() {
        val content = buildContentString()
        if (content.isNotBlank()) {
            val logoBitmap = QrCodeGenerator.createLogoBitmap(
                logoType = selectedLogo.value,
                tintColor = foregroundColor.value,
                customBitmap = customLogoBitmap.value
            )
            val bitmap = QrCodeGenerator.generateQrBitmap(
                content = content,
                widthPx = 600,
                heightPx = 600,
                foregroundColor = foregroundColor.value,
                backgroundColor = backgroundColor.value,
                bodyShape = bodyShape.value,
                eyeShape = eyeShape.value,
                centerLogo = logoBitmap,
                stickerFrame = selectedSticker.value
            )
            _generatedBitmap.value = bitmap
        } else {
            _generatedBitmap.value = null
        }
    }

    private fun UriEncode(str: String): String {
        return java.net.URLEncoder.encode(str, "UTF-8")
    }
}

