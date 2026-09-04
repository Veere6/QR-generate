package com.example.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

enum class QrStickerFrame(val label: String) {
    NONE("None"),
    CIRCLE("Circle"),
    CORNER("Corner"),
    BOTTOM_BADGE("Badge"),
    TOP_BANNER("Banner"),
    SPEECH_BUBBLE("Bubble"),
    PHONE("Phone"),
    TICKET("Ticket"),
    COFFEE("Coffee"),
    RIBBON("Ribbon")
}

enum class QrBodyShape(val label: String) {
    SQUARE("Square"),
    DOTS("Dots"),
    ROUNDED("Rounded"),
    HEARTS("Hearts"),
    STARS("Stars"),
    TRIANGLES("Triangles")
}

enum class QrEyeShape(val label: String) {
    SQUARE("Square"),
    ROUNDED("Rounded"),
    CIRCLE("Circle"),
    LEAF("Leaf")
}

enum class QrLogoType(val label: String) {
    NONE("None"),
    SCAN_ME("Scan Me"),
    FACEBOOK("Facebook"),
    INSTAGRAM("Instagram"),
    LINKEDIN("LinkedIn"),
    TWITTER_X("X"),
    YOUTUBE("YouTube"),
    TIKTOK("TikTok"),
    PINTEREST("Pinterest"),
    APPLE("Apple"),
    GMAIL("Gmail"),
    BEHANCE("Behance"),
    STAR("Star"),
    SHOPPING("Shop"),
    PDF("PDF"),
    PERCENT("%"),
    CUSTOM("Custom Upload")
}

object QrCodeGenerator {

    fun createLogoBitmap(logoType: QrLogoType, tintColor: Color, customBitmap: Bitmap? = null): Bitmap? {
        if (logoType == QrLogoType.NONE) return null
        if (logoType == QrLogoType.CUSTOM) return customBitmap

        val size = 120
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val colorInt = tintColor.toArgb()

        val paint = Paint().apply {
            color = colorInt
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = 8f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        when (logoType) {
            QrLogoType.NONE, QrLogoType.CUSTOM -> return customBitmap
            QrLogoType.SCAN_ME -> {
                val bgPaint = Paint().apply { color = colorInt; isAntiAlias = true }
                val textPaint = Paint().apply {
                    color = AndroidColor.WHITE
                    isAntiAlias = true
                    textSize = 28f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawRoundRect(RectF(10f, 35f, 110f, 85f), 12f, 12f, bgPaint)
                canvas.drawText("SCAN", 60f, 60f, textPaint)
                canvas.drawText("ME", 60f, 80f, textPaint)
            }
            QrLogoType.FACEBOOK -> {
                val bgPaint = Paint().apply { color = 0xFF1877F2.toInt(); isAntiAlias = true }
                val textPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true; textSize = 90f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                canvas.drawCircle(60f, 60f, 55f, bgPaint)
                canvas.drawText("f", 48f, 95f, textPaint)
            }
            QrLogoType.INSTAGRAM -> {
                val p = Paint().apply { color = 0xFFE4405F.toInt(); isAntiAlias = true; style = Paint.Style.STROKE; strokeWidth = 10f }
                canvas.drawRoundRect(RectF(20f, 20f, 100f, 100f), 25f, 25f, p)
                canvas.drawCircle(60f, 60f, 22f, p)
                p.style = Paint.Style.FILL
                canvas.drawCircle(82f, 38f, 6f, p)
            }
            QrLogoType.LINKEDIN -> {
                val bgPaint = Paint().apply { color = 0xFF0A66C2.toInt(); isAntiAlias = true }
                val textPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true; textSize = 65f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                canvas.drawRoundRect(RectF(15f, 15f, 105f, 105f), 18f, 18f, bgPaint)
                canvas.drawText("in", 38f, 82f, textPaint)
            }
            QrLogoType.TWITTER_X -> {
                val bgPaint = Paint().apply { color = 0xFF000000.toInt(); isAntiAlias = true }
                val textPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true; textSize = 80f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                canvas.drawCircle(60f, 60f, 55f, bgPaint)
                canvas.drawText("X", 35f, 88f, textPaint)
            }
            QrLogoType.YOUTUBE -> {
                val bgPaint = Paint().apply { color = 0xFFFF0000.toInt(); isAntiAlias = true }
                val playPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true }
                canvas.drawRoundRect(RectF(15f, 30f, 105f, 90f), 20f, 20f, bgPaint)
                val path = Path().apply {
                    moveTo(50f, 45f)
                    lineTo(80f, 60f)
                    lineTo(50f, 75f)
                    close()
                }
                canvas.drawPath(path, playPaint)
            }
            QrLogoType.TIKTOK -> {
                val bgPaint = Paint().apply { color = 0xFF000000.toInt(); isAntiAlias = true }
                val tPaint = Paint().apply { color = 0xFF00F2FE.toInt(); isAntiAlias = true; textSize = 75f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                canvas.drawCircle(60f, 60f, 55f, bgPaint)
                canvas.drawText("♪", 40f, 88f, tPaint)
            }
            QrLogoType.PINTEREST -> {
                val bgPaint = Paint().apply { color = 0xFFBD081C.toInt(); isAntiAlias = true }
                val tPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true; textSize = 80f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                canvas.drawCircle(60f, 60f, 55f, bgPaint)
                canvas.drawText("P", 40f, 88f, tPaint)
            }
            QrLogoType.APPLE -> {
                val bgPaint = Paint().apply { color = 0xFF333333.toInt(); isAntiAlias = true }
                val tPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true; textSize = 70f }
                canvas.drawCircle(60f, 60f, 55f, bgPaint)
                canvas.drawText("", 38f, 85f, tPaint)
            }
            QrLogoType.GMAIL -> {
                val bgPaint = Paint().apply { color = 0xFFEA4335.toInt(); isAntiAlias = true }
                val tPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true; textSize = 70f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                canvas.drawRoundRect(RectF(15f, 25f, 105f, 95f), 15f, 15f, bgPaint)
                canvas.drawText("M", 35f, 82f, tPaint)
            }
            QrLogoType.BEHANCE -> {
                val bgPaint = Paint().apply { color = 0xFF1769FF.toInt(); isAntiAlias = true }
                val tPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true; textSize = 50f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                canvas.drawCircle(60f, 60f, 55f, bgPaint)
                canvas.drawText("Bē", 28f, 78f, tPaint)
            }
            QrLogoType.STAR -> {
                val path = Path()
                val cx = 60f; val cy = 60f; val outer = 50f; val inner = 22f
                for (i in 0 until 10) {
                    val r = if (i % 2 == 0) outer else inner
                    val angle = Math.toRadians((i * 36 - 90).toDouble())
                    val x = (cx + r * Math.cos(angle)).toFloat()
                    val y = (cy + r * Math.sin(angle)).toFloat()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                canvas.drawPath(path, paint)
            }
            QrLogoType.SHOPPING -> {
                paint.style = Paint.Style.STROKE
                canvas.drawRoundRect(RectF(25f, 45f, 95f, 105f), 10f, 10f, paint)
                canvas.drawArc(RectF(40f, 25f, 80f, 60f), 180f, 180f, false, paint)
            }
            QrLogoType.PDF -> {
                val bgPaint = Paint().apply { color = 0xFFE53935.toInt(); isAntiAlias = true }
                val textPaint = Paint().apply { color = AndroidColor.WHITE; isAntiAlias = true; textSize = 32f; typeface = android.graphics.Typeface.DEFAULT_BOLD }
                canvas.drawRoundRect(RectF(15f, 30f, 105f, 90f), 15f, 15f, bgPaint)
                canvas.drawText("PDF", 30f, 68f, textPaint)
            }
            QrLogoType.PERCENT -> {
                paint.textSize = 55f
                paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("%", 60f, 80f, paint)
            }
        }
        return bitmap
    }

    fun generateQrBitmap(
        content: String,
        widthPx: Int = 600,
        heightPx: Int = 600,
        foregroundColor: Color = Color(0xFF6C3BFF),
        backgroundColor: Color = Color(0xFFFFFFFF),
        bodyShape: QrBodyShape = QrBodyShape.SQUARE,
        eyeShape: QrEyeShape = QrEyeShape.SQUARE,
        centerLogo: Bitmap? = null,
        stickerFrame: QrStickerFrame = QrStickerFrame.NONE
    ): Bitmap? {
        if (content.isBlank()) return null

        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 1)
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            }

            val writer = QRCodeWriter()
            // Generate unscaled bit matrix to get actual module count
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 0, 0, hints)

            val numModules = bitMatrix.width
            val moduleSize = widthPx.toFloat() / numModules

            val qrBitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(qrBitmap)
            val fgArgb = foregroundColor.toArgb()
            val bgArgb = backgroundColor.toArgb()

            val bgPaint = Paint().apply { color = bgArgb; style = Paint.Style.FILL }
            val fgPaint = Paint().apply { color = fgArgb; style = Paint.Style.FILL; isAntiAlias = true }

            canvas.drawRect(0f, 0f, widthPx.toFloat(), heightPx.toFloat(), bgPaint)

            // Helper to check if (x,y) (in modules) is within any 7x7 corner finder pattern
            fun isFinderPattern(x: Int, y: Int): Boolean {
                val isTopLeft = x < 7 && y < 7
                val isTopRight = x >= numModules - 7 && y < 7
                val isBottomLeft = x < 7 && y >= numModules - 7
                return isTopLeft || isTopRight || isBottomLeft
            }

            // Draw Body Modules
            for (y in 0 until numModules) {
                for (x in 0 until numModules) {
                    if (bitMatrix.get(x, y) && !isFinderPattern(x, y)) {
                        val left = x * moduleSize
                        val top = y * moduleSize
                        val right = left + moduleSize
                        val bottom = top + moduleSize
                        val cx = left + moduleSize / 2f
                        val cy = top + moduleSize / 2f

                        when (bodyShape) {
                            QrBodyShape.SQUARE -> {
                                canvas.drawRect(left, top, right, bottom, fgPaint)
                            }
                            QrBodyShape.DOTS -> {
                                canvas.drawCircle(cx, cy, moduleSize * 0.45f, fgPaint)
                            }
                            QrBodyShape.ROUNDED -> {
                                canvas.drawRoundRect(RectF(left, top, right, bottom), moduleSize * 0.35f, moduleSize * 0.35f, fgPaint)
                            }
                            QrBodyShape.HEARTS -> {
                                val heartPath = Path().apply {
                                    moveTo(cx, bottom - moduleSize * 0.1f)
                                    cubicTo(left, cy + moduleSize * 0.2f, left, top + moduleSize * 0.1f, cx - moduleSize * 0.25f, top + moduleSize * 0.1f)
                                    cubicTo(cx - moduleSize * 0.4f, top + moduleSize * 0.1f, cx, cy - moduleSize * 0.1f, cx, cy)
                                    cubicTo(cx, cy - moduleSize * 0.1f, cx + moduleSize * 0.4f, top + moduleSize * 0.1f, cx + moduleSize * 0.25f, top + moduleSize * 0.1f)
                                    cubicTo(right, top + moduleSize * 0.1f, right, cy + moduleSize * 0.2f, cx, bottom - moduleSize * 0.1f)
                                    close()
                                }
                                canvas.drawPath(heartPath, fgPaint)
                            }
                            QrBodyShape.STARS -> {
                                val starPath = Path()
                                for (i in 0 until 10) {
                                    val r = if (i % 2 == 0) moduleSize * 0.45f else moduleSize * 0.2f
                                    val angle = Math.toRadians((i * 36 - 90).toDouble())
                                    val sx = (cx + r * Math.cos(angle)).toFloat()
                                    val sy = (cy + r * Math.sin(angle)).toFloat()
                                    if (i == 0) starPath.moveTo(sx, sy) else starPath.lineTo(sx, sy)
                                }
                                starPath.close()
                                canvas.drawPath(starPath, fgPaint)
                            }
                            QrBodyShape.TRIANGLES -> {
                                val triPath = Path().apply {
                                    moveTo(cx, top + moduleSize * 0.1f)
                                    lineTo(right - moduleSize * 0.1f, bottom - moduleSize * 0.1f)
                                    lineTo(left + moduleSize * 0.1f, bottom - moduleSize * 0.1f)
                                    close()
                                }
                                canvas.drawPath(triPath, fgPaint)
                            }
                        }
                    }
                }
            }

            // Draw Custom Corner Eyes (Finder Patterns)
            fun drawEye(startModuleX: Float, startModuleY: Float, eyeWidthModules: Float) {
                val startX = startModuleX * moduleSize
                val startY = startModuleY * moduleSize
                val eyeWidth = eyeWidthModules * moduleSize
                val cell = eyeWidth / 7f
                val outerRect = RectF(startX, startY, startX + eyeWidth, startY + eyeWidth)
                val innerRect = RectF(startX + cell * 2f, startY + cell * 2f, startX + cell * 5f, startY + cell * 5f)

                val eyeStrokePaint = Paint().apply {
                    color = fgArgb
                    style = Paint.Style.STROKE
                    strokeWidth = cell * 1f
                    isAntiAlias = true
                }
                val eyeFillPaint = Paint().apply {
                    color = fgArgb
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                val eyeBgPaint = Paint().apply {
                    color = bgArgb
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }

                val strokeRect = RectF(
                    startX + cell * 0.5f,
                    startY + cell * 0.5f,
                    startX + eyeWidth - cell * 0.5f,
                    startY + eyeWidth - cell * 0.5f
                )

                // Clear background for finder square
                canvas.drawRect(outerRect, eyeBgPaint)

                when (eyeShape) {
                    QrEyeShape.SQUARE -> {
                        canvas.drawRect(strokeRect, eyeStrokePaint)
                        canvas.drawRect(innerRect, eyeFillPaint)
                    }
                    QrEyeShape.ROUNDED -> {
                        canvas.drawRoundRect(strokeRect, cell * 1.8f, cell * 1.8f, eyeStrokePaint)
                        canvas.drawRoundRect(innerRect, cell * 1f, cell * 1f, eyeFillPaint)
                    }
                    QrEyeShape.CIRCLE -> {
                        val cx = startX + eyeWidth / 2f
                        val cy = startY + eyeWidth / 2f
                        canvas.drawCircle(cx, cy, cell * 3f, eyeStrokePaint)
                        canvas.drawCircle(cx, cy, cell * 1.5f, eyeFillPaint)
                    }
                    QrEyeShape.LEAF -> {
                        val path = Path().apply {
                            moveTo(strokeRect.left + cell * 1.5f, strokeRect.top)
                            lineTo(strokeRect.right, strokeRect.top)
                            lineTo(strokeRect.right, strokeRect.bottom - cell * 1.5f)
                            arcTo(RectF(strokeRect.right - cell * 3f, strokeRect.bottom - cell * 3f, strokeRect.right, strokeRect.bottom), 0f, 90f, false)
                            lineTo(strokeRect.left, strokeRect.bottom)
                            lineTo(strokeRect.left, strokeRect.top + cell * 1.5f)
                            arcTo(RectF(strokeRect.left, strokeRect.top, strokeRect.left + cell * 3f, strokeRect.top + cell * 3f), 180f, 90f, false)
                            close()
                        }
                        canvas.drawPath(path, eyeStrokePaint)
                        canvas.drawRoundRect(innerRect, cell * 1.2f, cell * 1.2f, eyeFillPaint)
                    }
                }
            }

            drawEye(0f, 0f, 7f)
            drawEye((numModules - 7).toFloat(), 0f, 7f)
            drawEye(0f, (numModules - 7).toFloat(), 7f)

            // Overlay logo if provided
            if (centerLogo != null) {
                val logoWidth = qrBitmap.width / 5
                val logoHeight = qrBitmap.height / 5
                val left = (qrBitmap.width - logoWidth) / 2f
                val top = (qrBitmap.height - logoHeight) / 2f

                val bgCirclePaint = Paint().apply { color = bgArgb; isAntiAlias = true; style = Paint.Style.FILL }
                val borderPaint = Paint().apply { color = fgArgb; isAntiAlias = true; style = Paint.Style.STROKE; strokeWidth = 3f }

                val centerX = qrBitmap.width / 2f
                val centerY = qrBitmap.height / 2f
                val radius = (logoWidth / 2f) + 8f

                canvas.drawCircle(centerX, centerY, radius, bgCirclePaint)
                canvas.drawCircle(centerX, centerY, radius, borderPaint)

                val scaledLogo = Bitmap.createScaledBitmap(centerLogo, logoWidth, logoHeight, true)
                canvas.drawBitmap(scaledLogo, left, top, null)
            }

            // Apply Sticker Frame Wrapper if selected
            if (stickerFrame != QrStickerFrame.NONE) {
                return wrapInStickerFrame(qrBitmap, stickerFrame, foregroundColor, backgroundColor)
            }

            qrBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun wrapInStickerFrame(
        qrBitmap: Bitmap,
        frame: QrStickerFrame,
        fgColor: Color,
        bgColor: Color
    ): Bitmap {
        val qrSize = qrBitmap.width
        val padding = 60
        val bottomExtra = 120
        val topExtra = if (frame == QrStickerFrame.TOP_BANNER) 120 else 60

        val totalWidth = qrSize + (padding * 2)
        val totalHeight = qrSize + padding + topExtra + bottomExtra

        val framedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(framedBitmap)

        val fgArgb = fgColor.toArgb()
        val bgArgb = bgColor.toArgb()

        val bgPaint = Paint().apply { color = bgArgb; style = Paint.Style.FILL; isAntiAlias = true }
        val fgPaint = Paint().apply { color = fgArgb; style = Paint.Style.FILL; isAntiAlias = true }
        val strokePaint = Paint().apply {
            color = fgArgb
            style = Paint.Style.STROKE
            strokeWidth = 14f
            isAntiAlias = true
        }
        val dashedStrokePaint = Paint().apply {
            color = fgArgb
            style = Paint.Style.STROKE
            strokeWidth = 10f
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(20f, 20f), 0f)
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = AndroidColor.WHITE
            isAntiAlias = true
            textSize = 38f
            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val qrLeft = padding.toFloat()
        val qrTop = topExtra.toFloat()

        when (frame) {
            QrStickerFrame.CIRCLE -> {
                val cx = totalWidth / 2f
                val cy = totalHeight / 2f
                val radius = (totalWidth / 2f) - 16f
                canvas.drawCircle(cx, cy, radius, bgPaint)
                canvas.drawCircle(cx, cy, radius, dashedStrokePaint)
                canvas.drawBitmap(qrBitmap, qrLeft, (totalHeight - qrSize) / 2f - 40f, null)

                val badgeRect = RectF(cx - 120f, totalHeight - 140f, cx + 120f, totalHeight - 60f)
                canvas.drawRoundRect(badgeRect, 40f, 40f, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 88f, textPaint)
            }
            QrStickerFrame.CORNER -> {
                val outerRect = RectF(20f, 20f, totalWidth - 20f, totalHeight - 20f)
                canvas.drawRoundRect(outerRect, 40f, 40f, bgPaint)

                // Corner brackets
                val cornerLength = 80f
                val p = Path()
                // Top Left
                p.moveTo(outerRect.left, outerRect.top + cornerLength)
                p.lineTo(outerRect.left, outerRect.top)
                p.lineTo(outerRect.left + cornerLength, outerRect.top)
                // Top Right
                p.moveTo(outerRect.right - cornerLength, outerRect.top)
                p.lineTo(outerRect.right, outerRect.top)
                p.lineTo(outerRect.right, outerRect.top + cornerLength)
                // Bottom Left
                p.moveTo(outerRect.left, outerRect.bottom - cornerLength)
                p.lineTo(outerRect.left, outerRect.bottom)
                p.lineTo(outerRect.left + cornerLength, outerRect.bottom)
                // Bottom Right
                p.moveTo(outerRect.right - cornerLength, outerRect.bottom)
                p.lineTo(outerRect.right, outerRect.bottom)
                p.lineTo(outerRect.right, outerRect.bottom - cornerLength)

                canvas.drawPath(p, strokePaint)

                canvas.drawBitmap(qrBitmap, qrLeft, qrTop, null)

                val cx = totalWidth / 2f
                val badgeRect = RectF(cx - 120f, totalHeight - 130f, cx + 120f, totalHeight - 50f)
                canvas.drawRoundRect(badgeRect, 40f, 40f, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 78f, textPaint)
            }
            QrStickerFrame.BOTTOM_BADGE -> {
                val rect = RectF(20f, 20f, totalWidth - 20f, totalHeight - 20f)
                canvas.drawRoundRect(rect, 30f, 30f, bgPaint)
                canvas.drawRoundRect(rect, 30f, 30f, strokePaint)

                canvas.drawBitmap(qrBitmap, qrLeft, qrTop, null)

                val cx = totalWidth / 2f
                val badgeRect = RectF(20f, totalHeight - 120f, totalWidth - 20f, totalHeight - 20f)

                // Draw rounded bottom only badge
                val badgePath = Path().apply {
                    moveTo(badgeRect.left, badgeRect.top)
                    lineTo(badgeRect.right, badgeRect.top)
                    lineTo(badgeRect.right, badgeRect.bottom - 30f)
                    arcTo(RectF(badgeRect.right - 60f, badgeRect.bottom - 60f, badgeRect.right, badgeRect.bottom), 0f, 90f, false)
                    lineTo(badgeRect.left + 30f, badgeRect.bottom)
                    arcTo(RectF(badgeRect.left, badgeRect.bottom - 60f, badgeRect.left + 60f, badgeRect.bottom), 90f, 90f, false)
                    close()
                }
                canvas.drawPath(badgePath, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 55f, textPaint)
            }
            QrStickerFrame.TOP_BANNER -> {
                val rect = RectF(20f, 20f, totalWidth - 20f, totalHeight - 20f)
                canvas.drawRoundRect(rect, 40f, 40f, bgPaint)
                canvas.drawRoundRect(rect, 40f, 40f, strokePaint)

                val cx = totalWidth / 2f
                val bannerRect = RectF(40f, 0f, totalWidth - 40f, 100f)

                val bannerPath = Path().apply {
                    moveTo(bannerRect.left, bannerRect.top)
                    lineTo(bannerRect.right, bannerRect.top)
                    lineTo(bannerRect.right, bannerRect.bottom)
                    lineTo(cx, bannerRect.bottom - 20f) // Banner cut
                    lineTo(bannerRect.left, bannerRect.bottom)
                    close()
                }

                canvas.drawPath(bannerPath, fgPaint)
                canvas.drawText("SCAN ME", cx, 65f, textPaint)

                canvas.drawBitmap(qrBitmap, qrLeft, qrTop + 30f, null)
            }
            QrStickerFrame.SPEECH_BUBBLE -> {
                val bubbleRect = RectF(20f, 20f, totalWidth - 20f, totalHeight - 100f)
                canvas.drawRoundRect(bubbleRect, 60f, 60f, bgPaint)
                canvas.drawRoundRect(bubbleRect, 60f, 60f, strokePaint)

                val tail = Path().apply {
                    moveTo(totalWidth / 2f - 40f, totalHeight - 100f)
                    lineTo(totalWidth / 2f, totalHeight - 20f)
                    lineTo(totalWidth / 2f + 40f, totalHeight - 100f)
                    close()
                }
                canvas.drawPath(tail, bgPaint)

                val tailStroke = Path().apply {
                    moveTo(totalWidth / 2f - 40f, totalHeight - 100f)
                    lineTo(totalWidth / 2f, totalHeight - 20f)
                    lineTo(totalWidth / 2f + 40f, totalHeight - 100f)
                }
                canvas.drawPath(tailStroke, strokePaint)

                // Overwrite the line between bubble and tail
                val clearPaint = Paint(bgPaint).apply { strokeWidth = 16f; style = Paint.Style.STROKE }
                canvas.drawLine(totalWidth / 2f - 38f, totalHeight - 100f, totalWidth / 2f + 38f, totalHeight - 100f, clearPaint)

                canvas.drawBitmap(qrBitmap, qrLeft, qrTop, null)

                val cx = totalWidth / 2f
                val badgeRect = RectF(cx - 110f, totalHeight - 200f, cx + 110f, totalHeight - 130f)
                canvas.drawRoundRect(badgeRect, 35f, 35f, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 152f, textPaint)
            }
            QrStickerFrame.PHONE -> {
                val phoneRect = RectF(30f, 10f, totalWidth - 30f, totalHeight - 10f)
                canvas.drawRoundRect(phoneRect, 60f, 60f, bgPaint)
                canvas.drawRoundRect(phoneRect, 60f, 60f, strokePaint)

                // Speaker notch
                canvas.drawRoundRect(RectF(totalWidth / 2f - 50f, 35f, totalWidth / 2f + 50f, 45f), 5f, 5f, fgPaint)
                canvas.drawBitmap(qrBitmap, qrLeft, qrTop + 10f, null)

                val cx = totalWidth / 2f
                // Home button / Scan button
                val badgeRect = RectF(cx - 120f, totalHeight - 120f, cx + 120f, totalHeight - 40f)
                canvas.drawRoundRect(badgeRect, 40f, 40f, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 68f, textPaint)
            }
            QrStickerFrame.TICKET -> {
                val rect = RectF(20f, 20f, totalWidth - 20f, totalHeight - 20f)
                canvas.drawRoundRect(rect, 30f, 30f, bgPaint)
                canvas.drawRoundRect(rect, 30f, 30f, strokePaint)

                // Cutouts for ticket using CLEAR xfermode
                val clearCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.Transparent.toArgb()
                    xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(20f, totalHeight / 2f, 40f, clearCirclePaint)
                canvas.drawCircle(totalWidth - 20f, totalHeight / 2f, 40f, clearCirclePaint)

                // Draw stroke around cutouts
                val arcPaint = Paint(strokePaint).apply { style = Paint.Style.STROKE }
                canvas.drawArc(RectF(-20f, totalHeight / 2f - 40f, 60f, totalHeight / 2f + 40f), -90f, 180f, false, arcPaint)
                canvas.drawArc(RectF(totalWidth - 60f, totalHeight / 2f - 40f, totalWidth + 20f, totalHeight / 2f + 40f), 90f, 180f, false, arcPaint)

                val cx = totalWidth / 2f
                canvas.drawLine(80f, totalHeight / 2f, totalWidth - 80f, totalHeight / 2f, dashedStrokePaint)

                canvas.drawBitmap(qrBitmap, qrLeft, qrTop - 40f, null)

                val badgeRect = RectF(cx - 120f, totalHeight - 130f, cx + 120f, totalHeight - 50f)
                canvas.drawRoundRect(badgeRect, 40f, 40f, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 78f, textPaint)
            }
            QrStickerFrame.COFFEE -> {
                val cupRect = RectF(40f, 60f, totalWidth - 40f, totalHeight - 40f)
                
                // Draw cup path
                val cupPath = Path().apply {
                    moveTo(30f, 60f)
                    lineTo(totalWidth - 30f, 60f)
                    lineTo(totalWidth - 60f, totalHeight - 60f)
                    arcTo(RectF(60f, totalHeight - 100f, totalWidth - 60f, totalHeight - 20f), 0f, 180f, false)
                    lineTo(60f, totalHeight - 60f)
                    close()
                }
                
                canvas.drawPath(cupPath, bgPaint)
                canvas.drawPath(cupPath, strokePaint)

                // Lid
                val lidRect = RectF(20f, 20f, totalWidth - 20f, 60f)
                canvas.drawRoundRect(lidRect, 10f, 10f, bgPaint)
                canvas.drawRoundRect(lidRect, 10f, 10f, strokePaint)

                val lidTop = RectF(60f, 0f, totalWidth - 60f, 20f)
                canvas.drawRoundRect(lidTop, 10f, 10f, bgPaint)
                canvas.drawRoundRect(lidTop, 10f, 10f, strokePaint)

                // QR
                canvas.drawBitmap(qrBitmap, qrLeft, qrTop + 30f, null)

                val cx = totalWidth / 2f
                val badgeRect = RectF(cx - 110f, totalHeight - 160f, cx + 110f, totalHeight - 90f)
                canvas.drawRoundRect(badgeRect, 35f, 35f, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 112f, textPaint)
            }
            QrStickerFrame.RIBBON -> {
                val rect = RectF(20f, 20f, totalWidth - 20f, totalHeight - 60f)
                canvas.drawRoundRect(rect, 40f, 40f, bgPaint)
                canvas.drawRoundRect(rect, 40f, 40f, strokePaint)

                // Ribbons at bottom
                val ribbonPath = Path().apply {
                    moveTo(40f, totalHeight - 60f)
                    lineTo(40f, totalHeight.toFloat())
                    lineTo(70f, totalHeight - 30f)
                    lineTo(100f, totalHeight.toFloat())
                    lineTo(100f, totalHeight - 60f)
                    close()
                }
                canvas.drawPath(ribbonPath, fgPaint)
                
                val ribbonPathRight = Path().apply {
                    moveTo(totalWidth - 40f, totalHeight - 60f)
                    lineTo(totalWidth - 40f, totalHeight.toFloat())
                    lineTo(totalWidth - 70f, totalHeight - 30f)
                    lineTo(totalWidth - 100f, totalHeight.toFloat())
                    lineTo(totalWidth - 100f, totalHeight - 60f)
                    close()
                }
                canvas.drawPath(ribbonPathRight, fgPaint)

                canvas.drawBitmap(qrBitmap, qrLeft, qrTop, null)
                val cx = totalWidth / 2f
                val badgeRect = RectF(cx - 120f, totalHeight - 160f, cx + 120f, totalHeight - 80f)
                canvas.drawRoundRect(badgeRect, 40f, 40f, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 108f, textPaint)
            }
            else -> {
                val rect = RectF(20f, 20f, totalWidth - 20f, totalHeight - 20f)
                canvas.drawRoundRect(rect, 40f, 40f, bgPaint)
                canvas.drawRoundRect(rect, 40f, 40f, strokePaint)

                canvas.drawBitmap(qrBitmap, qrLeft, qrTop, null)

                val cx = totalWidth / 2f
                val badgeRect = RectF(cx - 120f, totalHeight - 130f, cx + 120f, totalHeight - 50f)
                canvas.drawRoundRect(badgeRect, 40f, 40f, fgPaint)
                canvas.drawText("SCAN ME", cx, totalHeight - 78f, textPaint)
            }
        }

        return framedBitmap
    }

    fun generateBarcodeBitmap(
        content: String,
        format: BarcodeFormat = BarcodeFormat.CODE_128,
        widthPx: Int = 600,
        heightPx: Int = 300,
        foregroundColor: Color = Color(0xFF000000),
        backgroundColor: Color = Color(0xFFFFFFFF)
    ): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
                put(EncodeHintType.MARGIN, 2)
            }
            val writer = MultiFormatWriter()
            val bitMatrix = writer.encode(content, format, widthPx, heightPx, hints)
            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height

            val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
            val fgArgb = foregroundColor.toArgb()
            val bgArgb = backgroundColor.toArgb()

            val pixels = IntArray(matrixWidth * matrixHeight)
            for (y in 0 until matrixHeight) {
                val offset = y * matrixWidth
                for (x in 0 until matrixWidth) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) fgArgb else bgArgb
                }
            }
            bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

