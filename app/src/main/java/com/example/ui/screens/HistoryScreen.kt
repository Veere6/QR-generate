package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScanEntity
import com.example.data.model.ScanType
import com.example.ui.components.EmptyState
import com.example.ui.components.GlassCard
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.OrangeBarcode
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.HistoryFilter
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.utils.Exporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    mainViewModel: MainViewModel,
    historyViewModel: HistoryViewModel,
    onSelectScanResult: (ScanEntity) -> Unit
) {
    val context = LocalContext.current
    val scans by historyViewModel.filteredScans.collectAsState()
    val searchQuery by historyViewModel.searchQuery.collectAsState()
    val selectedFilter by historyViewModel.selectedFilter.collectAsState()
    val isMultiSelectMode by historyViewModel.isMultiSelectMode.collectAsState()
    val selectedIds by historyViewModel.selectedIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(bottom = 90.dp)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scan History",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(
                    onClick = {
                        val file = Exporter.exportToCsv(context, scans)
                        if (file != null) {
                            Exporter.shareExportedFile(context, file)
                        } else {
                            Toast.makeText(context, "No items to export", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkCardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Export CSV",
                        tint = AccentGreen
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { historyViewModel.toggleMultiSelectMode() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isMultiSelectMode) PrimaryViolet else DarkCardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Multi Select Delete",
                        tint = Color.White
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { historyViewModel.searchQuery.value = it },
            placeholder = { Text("Search history...", color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryViolet,
                unfocusedBorderColor = DarkCardBorder,
                focusedContainerColor = DarkCardSurface,
                unfocusedContainerColor = DarkCardSurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HistoryFilter.values().forEach { filter ->
                val isSelected = selectedFilter == filter
                val labelText = when (filter) {
                    HistoryFilter.ALL -> "All"
                    HistoryFilter.QR_ONLY -> "QR Codes"
                    HistoryFilter.BARCODE_ONLY -> "Barcodes"
                    HistoryFilter.FAVORITES -> "Favorites"
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { historyViewModel.setFilter(filter) },
                    label = { Text(labelText, fontSize = 12.sp) },
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

        // Multi-select Delete Action Bar
        AnimatedVisibility(visible = isMultiSelectMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryViolet.copy(alpha = 0.2f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedIds.size} selected",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { historyViewModel.deleteSelected() },
                    enabled = selectedIds.isNotEmpty()
                ) {
                    Text("Delete Selected", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (scans.isEmpty()) {
            EmptyState(
                title = "No Scan History",
                subtitle = "Items you scan will automatically show up here.",
                icon = Icons.Outlined.History,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(scans, key = { it.id }) { scan ->
                    HistoryItemCard(
                        scan = scan,
                        isMultiSelectMode = isMultiSelectMode,
                        isSelected = selectedIds.contains(scan.id),
                        onToggleSelect = { historyViewModel.toggleSelection(scan.id) },
                        onToggleFavorite = { mainViewModel.toggleFavorite(scan) },
                        onClick = {
                            mainViewModel.setCurrentResult(scan)
                            onSelectScanResult(scan)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    scan: ScanEntity,
    isMultiSelectMode: Boolean,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(scan.timestamp))

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (isMultiSelectMode) onToggleSelect else onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMultiSelectMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryViolet)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (scan.scanType == ScanType.QR_CODE) AccentGreen.copy(alpha = 0.2f)
                        else OrangeBarcode.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (scan.scanType == ScanType.QR_CODE) Icons.Default.QrCode else Icons.Outlined.DocumentScanner,
                    contentDescription = null,
                    tint = if (scan.scanType == ScanType.QR_CODE) AccentGreen else OrangeBarcode,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${scan.scanType.name} • $dateStr",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (scan.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (scan.isFavorite) Color(0xFFFF5252) else TextMuted
                )
            }
        }
    }
}
