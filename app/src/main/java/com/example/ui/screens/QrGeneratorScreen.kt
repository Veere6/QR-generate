package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.OrangeBarcode
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GeneratorTab
import com.example.ui.viewmodel.GeneratorViewModel
import com.example.utils.QrBodyShape
import com.example.utils.QrEyeShape
import com.example.utils.QrLogoType
import com.example.utils.QrStickerFrame
import java.io.File
import java.io.FileOutputStream

enum class DesignSubTab(val label: String) {
    STICKER("STICKER"),
    COLOR("COLOR"),
    SHAPES("SHAPES"),
    LOGO("LOGO")
}

@Composable
fun QrGeneratorScreen(
    generatorViewModel: GeneratorViewModel,
    initialText: String = "",
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val selectedTab by generatorViewModel.selectedTab.collectAsState()
    val generatedBitmap by generatorViewModel.generatedBitmap.collectAsState()

    val foregroundColor by generatorViewModel.foregroundColor.collectAsState()
    val backgroundColor by generatorViewModel.backgroundColor.collectAsState()
    val selectedSticker by generatorViewModel.selectedSticker.collectAsState()
    val bodyShape by generatorViewModel.bodyShape.collectAsState()
    val eyeShape by generatorViewModel.eyeShape.collectAsState()
    val selectedLogo by generatorViewModel.selectedLogo.collectAsState()
    val customLogoBitmap by generatorViewModel.customLogoBitmap.collectAsState()
    val selectedPreset by generatorViewModel.selectedPreset.collectAsState()

    var showCustomizer by remember { mutableStateOf(true) }
    var designSubTab by remember { mutableStateOf(DesignSubTab.STICKER) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                generatorViewModel.setCustomLogo(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(initialText) {
        if (initialText.isNotBlank()) {
            generatorViewModel.textInput.value = initialText
            generatorViewModel.selectTab(GeneratorTab.TEXT)
        } else {
            generatorViewModel.generateQr()
        }
    }

    val presets = listOf("Classic", "Neon", "Emerald", "Sunset", "Cyberpunk", "Monochrome")

    val fgPalette = listOf(
        PrimaryViolet,
        SecondaryCyan,
        AccentGreen,
        OrangeBarcode,
        Color(0xFFFF0055),
        Color(0xFF2962FF),
        Color(0xFFFFD600),
        Color.Black
    )

    val bgPalette = listOf(
        Color.White,
        Color(0xFF0D111A),
        Color(0xFFF4F6F8),
        Color(0xFFFFF8E1),
        Color(0xFF121212)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkCardSurface)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "QR Generator",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { showCustomizer = !showCustomizer },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (showCustomizer) PrimaryViolet else DarkCardSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Customize",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // QR Preview Area
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(backgroundColor)
                    .border(width = 2.dp, color = PrimaryViolet.copy(alpha = 0.5f), shape = RoundedCornerShape(24.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (generatedBitmap != null) {
                    Image(
                        bitmap = generatedBitmap!!.asImageBitmap(),
                        contentDescription = "Generated QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Enter details below to generate QR",
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientButton(
                    text = "Save PNG",
                    icon = Icons.Default.Download,
                    modifier = Modifier.weight(1f).height(48.dp),
                    enabled = generatedBitmap != null,
                    onClick = {
                        if (generatedBitmap != null) {
                            saveBitmapToCacheAndShare(context, generatedBitmap!!, share = false)
                            Toast.makeText(context, "QR Code saved successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                GradientButton(
                    text = "Share",
                    icon = Icons.Default.Share,
                    modifier = Modifier.weight(1f).height(48.dp),
                    enabled = generatedBitmap != null,
                    onClick = {
                        if (generatedBitmap != null) {
                            saveBitmapToCacheAndShare(context, generatedBitmap!!, share = true)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs Horizontal Scroll
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp)
        ) {
            items(GeneratorTab.values()) { tab ->
                val isSelected = selectedTab == tab
                FilterChip(
                    selected = isSelected,
                    onClick = { generatorViewModel.selectTab(tab) },
                    label = {
                        Text(
                            text = tab.name,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryViolet,
                        selectedLabelColor = Color.White,
                        containerColor = DarkCardSurface,
                        labelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Contextual Input Fields Box
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when (selectedTab) {
                        GeneratorTab.URL -> {
                            val url by generatorViewModel.urlInput.collectAsState()
                            CustomTextField(
                                value = url,
                                onValueChange = {
                                    generatorViewModel.urlInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Website URL"
                            )
                        }

                        GeneratorTab.TEXT -> {
                            val text by generatorViewModel.textInput.collectAsState()
                            CustomTextField(
                                value = text,
                                onValueChange = {
                                    generatorViewModel.textInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Plain Text",
                                singleLine = false
                            )
                        }

                        GeneratorTab.PHONE -> {
                            val phone by generatorViewModel.phoneInput.collectAsState()
                            CustomTextField(
                                value = phone,
                                onValueChange = {
                                    generatorViewModel.phoneInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Phone Number"
                            )
                        }

                        GeneratorTab.SMS -> {
                            val smsPhone by generatorViewModel.smsPhoneInput.collectAsState()
                            val smsMessage by generatorViewModel.smsMessageInput.collectAsState()
                            CustomTextField(
                                value = smsPhone,
                                onValueChange = {
                                    generatorViewModel.smsPhoneInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Recipient Phone Number"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = smsMessage,
                                onValueChange = {
                                    generatorViewModel.smsMessageInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "SMS Message",
                                singleLine = false
                            )
                        }

                        GeneratorTab.EMAIL -> {
                            val email by generatorViewModel.emailInput.collectAsState()
                            val subject by generatorViewModel.emailSubjectInput.collectAsState()
                            CustomTextField(
                                value = email,
                                onValueChange = {
                                    generatorViewModel.emailInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Email Address"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = subject,
                                onValueChange = {
                                    generatorViewModel.emailSubjectInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Subject"
                            )
                        }

                        GeneratorTab.WIFI -> {
                            val ssid by generatorViewModel.wifiSsidInput.collectAsState()
                            val pass by generatorViewModel.wifiPasswordInput.collectAsState()
                            CustomTextField(
                                value = ssid,
                                onValueChange = {
                                    generatorViewModel.wifiSsidInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Network Name (SSID)"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = pass,
                                onValueChange = {
                                    generatorViewModel.wifiPasswordInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "WiFi Password"
                            )
                        }

                        GeneratorTab.CONTACT -> {
                            val name by generatorViewModel.contactNameInput.collectAsState()
                            val phone by generatorViewModel.contactPhoneInput.collectAsState()
                            val email by generatorViewModel.contactEmailInput.collectAsState()
                            CustomTextField(
                                value = name,
                                onValueChange = {
                                    generatorViewModel.contactNameInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Full Name"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = phone,
                                onValueChange = {
                                    generatorViewModel.contactPhoneInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Phone Number"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = email,
                                onValueChange = {
                                    generatorViewModel.contactEmailInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Email Address"
                            )
                        }

                        GeneratorTab.LOCATION -> {
                            val lat by generatorViewModel.latInput.collectAsState()
                            val lng by generatorViewModel.lngInput.collectAsState()
                            CustomTextField(
                                value = lat,
                                onValueChange = {
                                    generatorViewModel.latInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Latitude (e.g. 37.7749)"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = lng,
                                onValueChange = {
                                    generatorViewModel.lngInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Longitude (e.g. -122.4194)"
                            )
                        }

                        GeneratorTab.UPI -> {
                            val vpa by generatorViewModel.upiVpaInput.collectAsState()
                            val name by generatorViewModel.upiNameInput.collectAsState()
                            val amount by generatorViewModel.upiAmountInput.collectAsState()
                            CustomTextField(
                                value = vpa,
                                onValueChange = {
                                    generatorViewModel.upiVpaInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "UPI VPA ID (e.g. merchant@upi)"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = name,
                                onValueChange = {
                                    generatorViewModel.upiNameInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Payee Name"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = amount,
                                onValueChange = {
                                    generatorViewModel.upiAmountInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Amount (optional)"
                            )
                        }

                        GeneratorTab.EVENT -> {
                            val title by generatorViewModel.eventTitleInput.collectAsState()
                            val location by generatorViewModel.eventLocationInput.collectAsState()
                            CustomTextField(
                                value = title,
                                onValueChange = {
                                    generatorViewModel.eventTitleInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Event Title"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomTextField(
                                value = location,
                                onValueChange = {
                                    generatorViewModel.eventLocationInput.value = it
                                    generatorViewModel.generateQr()
                                },
                                label = "Event Location"
                            )
                        }
                    }
                }
            }

            // QR Customization Section
            Spacer(modifier = Modifier.height(16.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCustomizer = !showCustomizer },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = PrimaryViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2 Design QR Code (optional)",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (showCustomizer) "Hide" else "Show Options",
                            color = PrimaryViolet,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (showCustomizer) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Sub-Tabs Row (STICKER, COLOR, SHAPES, LOGO)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkCardSurface)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DesignSubTab.values().forEach { tab ->
                                val isSelected = designSubTab == tab
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) PrimaryViolet else Color.Transparent)
                                        .clickable { designSubTab = tab }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tab.label,
                                        color = if (isSelected) Color.White else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        when (designSubTab) {
                            DesignSubTab.STICKER -> {
                                Text(
                                    text = "Select Frame / Sticker",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(QrStickerFrame.values()) { sticker ->
                                        val isSelected = selectedSticker == sticker
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(DarkCardSurface)
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) AccentGreen else DarkCardBorder,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { generatorViewModel.setSticker(sticker) }
                                                .padding(6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.QrCode,
                                                    contentDescription = null,
                                                    tint = if (isSelected) AccentGreen else PrimaryViolet,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = sticker.label,
                                                    color = if (isSelected) Color.White else TextMuted,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            DesignSubTab.COLOR -> {
                                Text(
                                    text = "Style Presets",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(presets) { preset ->
                                        val isSelected = selectedPreset == preset
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { generatorViewModel.applyPreset(preset) },
                                            label = { Text(preset, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = PrimaryViolet,
                                                selectedLabelColor = Color.White,
                                                containerColor = DarkCardSurface,
                                                labelColor = TextSecondary
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Foreground Color",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(fgPalette) { color ->
                                        val isSelected = foregroundColor == color
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(
                                                    width = if (isSelected) 3.dp else 0.dp,
                                                    color = if (isSelected) Color.White else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    generatorViewModel.foregroundColor.value = color
                                                    generatorViewModel.selectedPreset.value = "Custom"
                                                    generatorViewModel.generateQr()
                                                }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Background Color",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(bgPalette) { color ->
                                        val isSelected = backgroundColor == color
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(
                                                    width = if (isSelected) 3.dp else 1.dp,
                                                    color = if (isSelected) PrimaryViolet else DarkCardBorder,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    generatorViewModel.backgroundColor.value = color
                                                    generatorViewModel.selectedPreset.value = "Custom"
                                                    generatorViewModel.generateQr()
                                                }
                                        )
                                    }
                                }
                            }

                            DesignSubTab.SHAPES -> {
                                Text(
                                    text = "Module Body Shape",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(QrBodyShape.values()) { shape ->
                                        val isSelected = bodyShape == shape
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { generatorViewModel.setBodyShape(shape) },
                                            label = { Text(shape.label, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = PrimaryViolet,
                                                selectedLabelColor = Color.White,
                                                containerColor = DarkCardSurface,
                                                labelColor = TextSecondary
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Corner Eyes Shape",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(QrEyeShape.values()) { shape ->
                                        val isSelected = eyeShape == shape
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { generatorViewModel.setEyeShape(shape) },
                                            label = { Text(shape.label, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = PrimaryViolet,
                                                selectedLabelColor = Color.White,
                                                containerColor = DarkCardSurface,
                                                labelColor = TextSecondary
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }

                            DesignSubTab.LOGO -> {
                                Text(
                                    text = "Select Logo Overlay",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(QrLogoType.values().filter { it != QrLogoType.CUSTOM }) { logo ->
                                        val isSelected = selectedLogo == logo
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(DarkCardSurface)
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) AccentGreen else DarkCardBorder,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { generatorViewModel.setLogo(logo) }
                                                .padding(6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                if (logo == QrLogoType.NONE) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = null,
                                                        tint = if (isSelected) AccentGreen else PrimaryViolet,
                                                        modifier = Modifier.size(28.dp)
                                                    )
                                                } else {
                                                    val logoBitmap = remember(logo) {
                                                        com.example.utils.QrCodeGenerator.createLogoBitmap(logo, PrimaryViolet, null)
                                                    }
                                                    if (logoBitmap != null) {
                                                        Image(
                                                            bitmap = logoBitmap.asImageBitmap(),
                                                            contentDescription = null,
                                                            modifier = Modifier.size(28.dp)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = logo.label,
                                                    color = if (isSelected) Color.White else TextMuted,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Custom Logo Upload Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(DarkCardSurface)
                                        .border(width = 1.dp, color = PrimaryViolet.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                                        .clickable { imagePickerLauncher.launch("image/*") }
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (customLogoBitmap != null && selectedLogo == QrLogoType.CUSTOM) {
                                            Image(
                                                bitmap = customLogoBitmap!!.asImageBitmap(),
                                                contentDescription = "Uploaded Logo",
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = "Custom Logo Uploaded",
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Tap to change image",
                                                    color = PrimaryViolet,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.AddPhotoAlternate,
                                                contentDescription = null,
                                                tint = PrimaryViolet,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = "Upload Custom Logo",
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "PNG, JPG, or SVG images",
                                                    color = TextMuted,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        singleLine = singleLine,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryViolet,
            unfocusedBorderColor = DarkCardBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF131622),
            unfocusedContainerColor = Color(0xFF131622)
        )
    )
}

fun saveBitmapToCacheAndShare(context: Context, bitmap: Bitmap, share: Boolean) {
    try {
        val file = File(context.cacheDir, "generated_qr_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        if (share) {
            val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share QR Code"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
