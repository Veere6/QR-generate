package com.example.utils

import com.example.data.model.ParsedCategory
import java.util.Locale

object ContentParser {

    fun parseCategory(content: String): ParsedCategory {
        val trimmed = content.trim()
        val lower = trimmed.lowercase(Locale.ROOT)

        return when {
            lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("www.") -> ParsedCategory.URL
            lower.startsWith("wifi:") -> ParsedCategory.WIFI
            lower.startsWith("upi://") || lower.contains("pay?") || lower.startsWith("upi:") -> ParsedCategory.UPI
            lower.startsWith("tel:") || lower.startsWith("phone:") -> ParsedCategory.PHONE
            lower.startsWith("smsto:") || lower.startsWith("sms:") -> ParsedCategory.SMS
            lower.startsWith("mailto:") || lower.startsWith("matmsg:") -> ParsedCategory.EMAIL
            lower.startsWith("begin:vcard") || lower.startsWith("begin:mecard") -> ParsedCategory.CONTACT
            lower.startsWith("geo:") || lower.contains("maps.google.com") || lower.contains("goo.gl/maps") -> ParsedCategory.LOCATION
            lower.startsWith("begin:vevent") -> ParsedCategory.CALENDAR
            lower.startsWith("bitcoin:") || lower.startsWith("ethereum:") -> ParsedCategory.CRYPTO
            isProductBarcode(trimmed) -> ParsedCategory.PRODUCT
            else -> ParsedCategory.TEXT
        }
    }

    private fun isProductBarcode(content: String): Boolean {
        // EAN-13, EAN-8, UPC-A, UPC-E numeric barcodes
        return content.all { it.isDigit() } && (content.length == 8 || content.length == 12 || content.length == 13 || content.length == 14)
    }

    fun parseTitle(content: String, category: ParsedCategory): String {
        val trimmed = content.trim()
        return when (category) {
            ParsedCategory.URL -> {
                val clean = trimmed.removePrefix("http://").removePrefix("https://").removePrefix("www.")
                clean.take(30)
            }
            ParsedCategory.WIFI -> {
                val ssid = extractWifiSsid(trimmed)
                if (ssid.isNotEmpty()) "WiFi: $ssid" else "WiFi Network"
            }
            ParsedCategory.UPI -> {
                val pa = extractQueryParam(trimmed, "pa")
                if (pa.isNotEmpty()) "UPI: $pa" else "UPI Payment"
            }
            ParsedCategory.PHONE -> "Phone: " + trimmed.removePrefix("tel:").removePrefix("phone:")
            ParsedCategory.SMS -> "SMS: " + trimmed.removePrefix("smsto:").removePrefix("sms:").take(25)
            ParsedCategory.EMAIL -> "Email: " + trimmed.removePrefix("mailto:").take(25)
            ParsedCategory.CONTACT -> "Contact Card"
            ParsedCategory.LOCATION -> "Location Coordinate"
            ParsedCategory.PRODUCT -> "Product Barcode ($trimmed)"
            ParsedCategory.CALENDAR -> "Calendar Event"
            ParsedCategory.CRYPTO -> "Crypto Wallet Address"
            ParsedCategory.TEXT -> if (trimmed.length > 30) trimmed.take(28) + "..." else trimmed
        }
    }

    fun extractWifiSsid(wifiContent: String): String {
        // e.g. WIFI:S:MySSID;P:MyPass;T:WPA;;
        val regex = Regex("S:([^;]+)")
        val match = regex.find(wifiContent)
        return match?.groupValues?.get(1) ?: ""
    }

    fun extractWifiPassword(wifiContent: String): String {
        val regex = Regex("P:([^;]+)")
        val match = regex.find(wifiContent)
        return match?.groupValues?.get(1) ?: ""
    }

    fun extractWifiType(wifiContent: String): String {
        val regex = Regex("T:([^;]+)")
        val match = regex.find(wifiContent)
        return match?.groupValues?.get(1) ?: "WPA/WPA2"
    }

    private fun extractQueryParam(url: String, param: String): String {
        val regex = Regex("$param=([^&]+)")
        val match = regex.find(url)
        return match?.groupValues?.get(1) ?: ""
    }
}
