package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ScanDatabase
import com.example.data.model.ScanEntity
import com.example.data.model.ScanType
import com.example.data.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class HistoryFilter {
    ALL, QR_ONLY, BARCODE_ONLY, FAVORITES
}

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val scanDao = ScanDatabase.getDatabase(application).scanDao()
    private val repository = ScanRepository(scanDao)

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow(HistoryFilter.ALL)
    val isMultiSelectMode = MutableStateFlow(false)
    val selectedIds = MutableStateFlow<Set<Long>>(emptySet())

    val filteredScans: StateFlow<List<ScanEntity>> = combine(
        repository.allScans,
        searchQuery,
        selectedFilter
    ) { scans, query, filter ->
        scans.filter { item ->
            // Filter by type/favorite
            val matchesFilter = when (filter) {
                HistoryFilter.ALL -> true
                HistoryFilter.QR_ONLY -> item.scanType == ScanType.QR_CODE
                HistoryFilter.BARCODE_ONLY -> item.scanType == ScanType.BARCODE
                HistoryFilter.FAVORITES -> item.isFavorite
            }

            // Filter by search query
            val matchesQuery = query.isBlank() ||
                    item.content.contains(query, ignoreCase = true) ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.category.name.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: HistoryFilter) {
        selectedFilter.value = filter
    }

    fun toggleSelection(id: Long) {
        val current = selectedIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        selectedIds.value = current
        if (current.isEmpty()) {
            isMultiSelectMode.value = false
        }
    }

    fun toggleMultiSelectMode() {
        val next = !isMultiSelectMode.value
        isMultiSelectMode.value = next
        if (!next) {
            selectedIds.value = emptySet()
        }
    }

    fun deleteSelected() {
        viewModelScope.launch {
            repository.deleteScansByIds(selectedIds.value.toList())
            selectedIds.value = emptySet()
            isMultiSelectMode.value = false
        }
    }

    fun deleteSingle(scan: ScanEntity) {
        viewModelScope.launch {
            repository.deleteScan(scan)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
