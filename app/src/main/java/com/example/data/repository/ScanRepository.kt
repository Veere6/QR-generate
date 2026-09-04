package com.example.data.repository

import com.example.data.local.ScanDao
import com.example.data.model.ScanEntity
import com.example.data.model.ScanType
import kotlinx.coroutines.flow.Flow

class ScanRepository(private val scanDao: ScanDao) {

    val allScans: Flow<List<ScanEntity>> = scanDao.getAllScans()
    val favorites: Flow<List<ScanEntity>> = scanDao.getFavorites()
    val scanCount: Flow<Int> = scanDao.getScanCount()
    val favoriteCount: Flow<Int> = scanDao.getFavoriteCount()

    fun getScansByType(type: ScanType): Flow<List<ScanEntity>> = scanDao.getScansByType(type)

    fun searchScans(query: String): Flow<List<ScanEntity>> = scanDao.searchScans(query)

    suspend fun getScanById(id: Long): ScanEntity? = scanDao.getScanById(id)

    suspend fun insertScan(scan: ScanEntity): Long = scanDao.insertScan(scan)

    suspend fun updateScan(scan: ScanEntity) = scanDao.updateScan(scan)

    suspend fun toggleFavorite(scan: ScanEntity) {
        scanDao.updateScan(scan.copy(isFavorite = !scan.isFavorite))
    }

    suspend fun deleteScan(scan: ScanEntity) = scanDao.deleteScan(scan)

    suspend fun deleteScansByIds(ids: List<Long>) = scanDao.deleteScansByIds(ids)

    suspend fun clearAll() = scanDao.clearAll()
}
