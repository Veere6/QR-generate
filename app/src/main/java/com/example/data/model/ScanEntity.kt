package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val title: String,
    val scanType: ScanType,
    val format: BarcodeFormatType,
    val category: ParsedCategory,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val customNote: String = ""
)
