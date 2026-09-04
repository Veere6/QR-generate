package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.OrangeBarcode

@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    frameColor: Color = AccentGreen,
    isBarcodeMode: Boolean = false,
    animateScanLine: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laserAnimation")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Square frame for QR, Rectangular for Barcode
        val boxWidth = if (isBarcodeMode) width * 0.85f else width * 0.72f
        val boxHeight = if (isBarcodeMode) width * 0.48f else width * 0.72f

        val left = (width - boxWidth) / 2f
        val top = (height - boxHeight) / 2.3f
        val right = left + boxWidth
        val bottom = top + boxHeight

        val cornerRadius = 24.dp.toPx()
        val cornerLength = 36.dp.toPx()
        val strokeWidth = 4.dp.toPx()

        // Semi-transparent dim background layer
        drawRect(color = Color.Black.copy(alpha = 0.62f))

        // Cut out transparent scanning window
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(boxWidth, boxHeight),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            blendMode = BlendMode.Clear
        )

        // Draw Corner Frames
        val cornerPath = Path().apply {
            // Top-Left Corner
            moveTo(left, top + cornerLength)
            lineTo(left, top + cornerRadius)
            quadraticTo(left, top, left + cornerRadius, top)
            lineTo(left + cornerLength, top)

            // Top-Right Corner
            moveTo(right - cornerLength, top)
            lineTo(right - cornerRadius, top)
            quadraticTo(right, top, right, top + cornerRadius)
            lineTo(right, top + cornerLength)

            // Bottom-Right Corner
            moveTo(right, bottom - cornerLength)
            lineTo(right, bottom - cornerRadius)
            quadraticTo(right, bottom, right - cornerRadius, bottom)
            lineTo(right - cornerLength, bottom)

            // Bottom-Left Corner
            moveTo(left + cornerLength, bottom)
            lineTo(left + cornerRadius, bottom)
            quadraticTo(left, bottom, left, bottom - cornerRadius)
            lineTo(left, bottom - cornerLength)
        }

        drawPath(
            path = cornerPath,
            color = frameColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Animated Scanning Line
        if (animateScanLine) {
            val laserY = top + (boxHeight * laserYRatio)
            val laserBrush = Brush.horizontalGradient(
                colors = listOf(
                    frameColor.copy(alpha = 0.1f),
                    frameColor,
                    frameColor,
                    frameColor.copy(alpha = 0.1f)
                )
            )

            drawLine(
                brush = laserBrush,
                start = Offset(left + 16.dp.toPx(), laserY),
                end = Offset(right - 16.dp.toPx(), laserY),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}
