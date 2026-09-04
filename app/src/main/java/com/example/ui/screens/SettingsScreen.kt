package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.SettingsViewModel
import com.example.utils.Exporter

@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val settings by settingsViewModel.settings.collectAsState()
    val allScans by mainViewModel.allScans.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 100.dp)
    ) {
        // Top Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Appearance & Behavior Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Scanner Preferences",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    SettingToggleRow(
                        title = "Dark Theme",
                        subtitle = "Always use premium dark theme",
                        icon = Icons.Default.Palette,
                        checked = settings.darkMode,
                        onCheckedChange = { settingsViewModel.setDarkMode(it) }
                    )
                    SettingToggleRow(
                        title = "Sound Feedback",
                        subtitle = "Play audio beep when code is detected",
                        icon = Icons.Default.VolumeUp,
                        checked = settings.soundEnabled,
                        onCheckedChange = { settingsViewModel.setSound(it) }
                    )
                    SettingToggleRow(
                        title = "Vibration Feedback",
                        subtitle = "Vibrate on successful scan",
                        icon = Icons.Default.Vibration,
                        checked = settings.vibrationEnabled,
                        onCheckedChange = { settingsViewModel.setVibration(it) }
                    )
                    SettingToggleRow(
                        title = "Scan Line Animation",
                        subtitle = "Animate laser overlay inside scanner box",
                        icon = Icons.Default.GraphicEq,
                        checked = settings.scanAnimationEnabled,
                        onCheckedChange = { settingsViewModel.setScanAnimation(it) }
                    )
                    SettingToggleRow(
                        title = "Auto Copy to Clipboard",
                        subtitle = "Automatically copy scanned QR/Barcode to clipboard",
                        icon = Icons.Default.ContentCopy,
                        checked = settings.autoCopy,
                        onCheckedChange = { settingsViewModel.setAutoCopy(it) }
                    )
                    SettingToggleRow(
                        title = "Auto Open Web Links",
                        subtitle = "Automatically open browser when URL scanned",
                        icon = Icons.Default.OpenInNew,
                        checked = settings.autoOpenUrl,
                        onCheckedChange = { settingsViewModel.setAutoOpenUrl(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Data & Privacy Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Data & Privacy",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    SettingActionRow(
                        title = "Backup History (CSV)",
                        subtitle = "Export all scan history to CSV file",
                        icon = Icons.Default.Download,
                        iconTint = AccentGreen,
                        onClick = {
                            val file = Exporter.exportToCsv(context, allScans)
                            if (file != null) {
                                Exporter.shareExportedFile(context, file)
                            } else {
                                Toast.makeText(context, "No history to export", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    SettingActionRow(
                        title = "Clear All Scan History",
                        subtitle = "Permanently remove all scan records",
                        icon = Icons.Default.DeleteForever,
                        iconTint = Color(0xFFFF5252),
                        onClick = { showClearDialog = true }
                    )
                    SettingActionRow(
                        title = "Privacy Policy",
                        subtitle = "100% On-device offline scanner privacy",
                        icon = Icons.Default.Lock,
                        iconTint = SecondaryCyan,
                        onClick = { showPrivacyDialog = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About & Support Section
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "About & Support",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    SettingActionRow(
                        title = "Share QR & Barcode Scanner",
                        subtitle = "Share app with friends and family",
                        icon = Icons.Default.Share,
                        iconTint = PrimaryViolet,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "Check out QR & Barcode Scanner app for fast scanning and generating QR codes!")
                            }
                            context.startActivity(Intent.createChooser(intent, "Share App"))
                        }
                    )
                    SettingActionRow(
                        title = "Rate App",
                        subtitle = "Support developer with 5 stars",
                        icon = Icons.Default.Star,
                        iconTint = Color(0xFFFFD600),
                        onClick = {
                            Toast.makeText(context, "Thank you for rating 5 stars!", Toast.LENGTH_SHORT).show()
                        }
                    )
                    SettingActionRow(
                        title = "Version",
                        subtitle = "1.0.0 (Production Build)",
                        icon = Icons.Default.Info,
                        iconTint = TextMuted,
                        onClick = {}
                    )
                }
            }
        }
    }

    // Clear History Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All History?", color = Color.White) },
            text = { Text("This will delete all saved scan history items. This action cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        mainViewModel.clearAllHistory()
                        showClearDialog = false
                        Toast.makeText(context, "All history cleared", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Clear All", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkCardSurface
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", color = Color.White) },
            text = {
                Text(
                    "QR Scanner & Barcode Scanner respects your privacy. All code processing and camera scanning is performed 100% locally on your device using Android ML Kit. No scan history or personal data is transmitted to remote servers.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Got It", color = PrimaryViolet, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkCardSurface
        )
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryViolet.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = PrimaryViolet, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryViolet)
        )
    }
}

@Composable
fun SettingActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = PrimaryViolet,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
            }
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}
