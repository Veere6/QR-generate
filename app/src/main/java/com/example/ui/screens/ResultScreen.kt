package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParsedCategory
import com.example.data.model.ScanEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.GradientButton
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.OrangeBarcode
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.utils.ContentParser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultScreen(
    mainViewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToGeneratorWithText: (String) -> Unit
) {
    val context = LocalContext.current
    val currentScanResult by mainViewModel.currentScanResult.collectAsState()

    var animateSuccess by remember { mutableStateOf(false) }
    val successScale by animateFloatAsState(
        targetValue = if (animateSuccess) 1f else 0.4f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "successAnim"
    )

    LaunchedEffect(Unit) {
        animateSuccess = true
    }

    val scan = currentScanResult ?: return

    val dateFormat = SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(scan.timestamp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
    ) {
        // Top Bar
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
                text = "Scan Details",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = {
                    mainViewModel.deleteScan(scan)
                    Toast.makeText(context, "Deleted from history", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(DarkCardSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFFF5252)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Success Animation Badge Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .scale(successScale)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentGreen.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AccentGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Scan Successful!",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryViolet.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = scan.category.name,
                        color = PrimaryViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = scan.format.name,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Content Card
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = scan.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF131622))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = scan.content,
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Scanned on $dateStr",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Action Row (Copy, Share, Favorite)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Copy Button
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Scanned Text", scan.content)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = SecondaryCyan
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Copy", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Share Button
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, scan.content)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Scanned Content"))
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = AccentGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Share", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Favorite Toggle Button
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { mainViewModel.toggleFavorite(scan) }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (scan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (scan.isFavorite) Color(0xFFFF5252) else TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (scan.isFavorite) "Favorited" else "Favorite",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contextual Main Action Button
            when (scan.category) {
                ParsedCategory.URL -> {
                    GradientButton(
                        text = "Open Website",
                        icon = Icons.AutoMirrored.Filled.OpenInNew,
                        onClick = {
                            val url = if (!scan.content.startsWith("http://") && !scan.content.startsWith("https://")) {
                                "https://${scan.content}"
                            } else scan.content
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                ParsedCategory.PHONE -> {
                    GradientButton(
                        text = "Call Phone Number",
                        icon = Icons.Outlined.Call,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${scan.content.removePrefix("tel:")}"))
                            context.startActivity(intent)
                        }
                    )
                }

                ParsedCategory.EMAIL -> {
                    GradientButton(
                        text = "Send Email",
                        icon = Icons.Outlined.Email,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${scan.content.removePrefix("mailto:")}"))
                            context.startActivity(intent)
                        }
                    )
                }

                ParsedCategory.LOCATION -> {
                    GradientButton(
                        text = "Open in Maps",
                        icon = Icons.Outlined.Map,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${scan.content}"))
                            context.startActivity(intent)
                        }
                    )
                }

                ParsedCategory.UPI -> {
                    GradientButton(
                        text = "Pay via UPI App",
                        icon = Icons.Outlined.Payment,
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scan.content))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No UPI Payment app found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                ParsedCategory.WIFI -> {
                    val ssid = ContentParser.extractWifiSsid(scan.content)
                    val pass = ContentParser.extractWifiPassword(scan.content)
                    GradientButton(
                        text = "Copy WiFi Password ($pass)",
                        icon = Icons.Outlined.Wifi,
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("WiFi Password", pass)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "WiFi password copied!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                else -> {
                    GradientButton(
                        text = "Generate QR Code for Text",
                        icon = Icons.Default.QrCode,
                        onClick = { onNavigateToGeneratorWithText(scan.content) }
                    )
                }
            }
        }
    }
}
