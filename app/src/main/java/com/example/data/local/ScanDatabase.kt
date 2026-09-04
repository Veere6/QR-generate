package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.BarcodeFormatType
import com.example.data.model.ParsedCategory
import com.example.data.model.ScanEntity
import com.example.data.model.ScanType

class Converters {
    @TypeConverter
    fun fromScanType(value: ScanType): String = value.name

    @TypeConverter
    fun toScanType(value: String): ScanType = try { ScanType.valueOf(value) } catch (e: Exception) { ScanType.QR_CODE }

    @TypeConverter
    fun fromFormat(value: BarcodeFormatType): String = value.name

    @TypeConverter
    fun toFormat(value: String): BarcodeFormatType = try { BarcodeFormatType.valueOf(value) } catch (e: Exception) { BarcodeFormatType.QR_CODE }

    @TypeConverter
    fun fromCategory(value: ParsedCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): ParsedCategory = try { ParsedCategory.valueOf(value) } catch (e: Exception) { ParsedCategory.TEXT }
}

@Database(entities = [ScanEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ScanDatabase : RoomDatabase() {

    abstract fun scanDao(): ScanDao

    companion object {
        @Volatile
        private var INSTANCE: ScanDatabase? = null

        fun getDatabase(context: Context): ScanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScanDatabase::class.java,
                    "qr_barcode_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
