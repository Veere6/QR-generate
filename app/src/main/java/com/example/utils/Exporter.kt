package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.ScanEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Exporter {

    fun exportToCsv(context: Context, items: List<ScanEntity>): File? {
        if (items.isEmpty()) return null
        return try {
            val fileName = "scan_history_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)
            val writer = FileOutputStream(file).bufferedWriter()

            writer.write("ID,Title,Type,Category,Format,Content,Favorite,Date\n")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            items.forEach { item ->
                val dateStr = dateFormat.format(Date(item.timestamp))
                val cleanContent = item.content.replace("\"", "\"\"")
                val cleanTitle = item.title.replace("\"", "\"\"")
                writer.write("${item.id},\"$cleanTitle\",${item.scanType},${item.category},${item.format},\"$cleanContent\",${item.isFavorite},\"$dateStr\"\n")
            }
            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareExportedFile(context: Context, file: File, mimeType: String = "text/csv") {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Export Scan History"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
