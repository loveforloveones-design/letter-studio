package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.SealColor
import com.example.model.SealStyle
import com.example.ui.theme.JpInk
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WaxSealBadge(
    style: SealStyle,
    sealColor: SealColor,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    monogramChar: String = "S"
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val baseColor = sealColor.color
            val darkRimColor = Color(
                red = (baseColor.red * 0.72f).coerceIn(0f, 1f),
                green = (baseColor.green * 0.72f).coerceIn(0f, 1f),
                blue = (baseColor.blue * 0.72f).coerceIn(0f, 1f)
            )
            val highlightColor = Color(
                red = (baseColor.red * 1.25f).coerceIn(0f, 1f),
                green = (baseColor.green * 1.25f).coerceIn(0f, 1f),
                blue = (baseColor.blue * 1.25f).coerceIn(0f, 1f),
                alpha = 0.55f
            )
            val strokeColor = JpInk

            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val r = (this.size.minDimension / 2f) * 0.88f

            // 1. Draw outer irregular wax drip puddle
            val puddlePath = Path()
            val points = 16
            for (i in 0 until points) {
                val angle = (i * (2 * Math.PI / points)).toFloat()
                // Small organic wobble
                val wobble = when (i % 4) {
                    0 -> 1.05f
                    1 -> 0.94f
                    2 -> 1.02f
                    else -> 0.97f
                }
                val px = cx + (r * wobble) * cos(angle)
                val py = cy + (r * wobble) * sin(angle)
                if (i == 0) puddlePath.moveTo(px, py) else puddlePath.lineTo(px, py)
            }
            puddlePath.close()

            // Outer shadow / edge line
            drawPath(
                path = puddlePath,
                color = strokeColor,
                style = Stroke(width = 2.4.dp.toPx(), join = StrokeJoin.Round)
            )
            // Wax fill
            drawPath(path = puddlePath, color = baseColor)

            // Inner debossed circle
            val innerR = r * 0.72f
            drawCircle(
                color = darkRimColor,
                radius = innerR,
                center = Offset(cx, cy),
                style = Stroke(width = 2.0.dp.toPx())
            )
            // Highlight arc
            drawArc(
                color = highlightColor,
                startAngle = 190f,
                sweepAngle = 100f,
                useCenter = false,
                topLeft = Offset(cx - innerR * 0.9f, cy - innerR * 0.9f),
                size = Size(innerR * 1.8f, innerR * 1.8f),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Center emblem by style
            when (style) {
                SealStyle.HEART -> drawSealHeart(cx, cy, innerR * 0.58f, strokeColor, darkRimColor)
                SealStyle.BOW -> drawSealBow(cx, cy, innerR * 0.62f, strokeColor, highlightColor)
                SealStyle.LILY -> drawSealLily(cx, cy, innerR * 0.65f, strokeColor)
                SealStyle.TEDDY -> drawSealTeddy(cx, cy, innerR * 0.62f, strokeColor)
                SealStyle.STAR -> drawSealStar(cx, cy, innerR * 0.60f, strokeColor)
                SealStyle.ROSE_CREST -> drawSealRoseCrest(cx, cy, innerR * 0.65f, strokeColor)
                SealStyle.MONOGRAM -> drawSealMonogram(cx, cy, innerR * 0.60f, strokeColor, monogramChar)
            }
        }
    }
}

private fun DrawScope.drawSealHeart(cx: Float, cy: Float, size: Float, strokeColor: Color, fillDetail: Color) {
    val path = Path().apply {
        moveTo(cx, cy + size * 0.85f)
        cubicTo(
            cx - size * 1.1f, cy + size * 0.25f,
            cx - size * 1.1f, cy - size * 0.75f,
            cx - size * 0.45f, cy - size * 0.75f
        )
        cubicTo(
            cx - size * 0.1f, cy - size * 0.75f,
            cx, cy - size * 0.45f,
            cx, cy - size * 0.45f
        )
        cubicTo(
            cx, cy - size * 0.45f,
            cx + size * 0.1f, cy - size * 0.75f,
            cx + size * 0.45f, cy - size * 0.75f
        )
        cubicTo(
            cx + size * 1.1f, cy - size * 0.75f,
            cx + size * 1.1f, cy + size * 0.25f,
            cx, cy + size * 0.85f
        )
        close()
    }
    drawPath(path = path, color = fillDetail)
    drawPath(path = path, color = strokeColor, style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round))
}

private fun DrawScope.drawSealBow(cx: Float, cy: Float, s: Float, strokeColor: Color, fillDetail: Color) {
    val bowPath = Path().apply {
        // Left loop
        moveTo(cx, cy)
        cubicTo(cx - s * 0.6f, cy - s * 0.8f, cx - s * 0.9f, cy - s * 0.1f, cx, cy)
        // Right loop
        cubicTo(cx + s * 0.6f, cy - s * 0.8f, cx + s * 0.9f, cy - s * 0.1f, cx, cy)
        // Ribbons
        moveTo(cx - s * 0.2f, cy + s * 0.1f)
        lineTo(cx - s * 0.6f, cy + s * 0.9f)
        lineTo(cx - s * 0.35f, cy + s * 0.8f)
        lineTo(cx - s * 0.15f, cy + s * 0.3f)

        moveTo(cx + s * 0.2f, cy + s * 0.1f)
        lineTo(cx + s * 0.6f, cy + s * 0.9f)
        lineTo(cx + s * 0.35f, cy + s * 0.8f)
        lineTo(cx + s * 0.15f, cy + s * 0.3f)
    }
    drawPath(path = bowPath, color = fillDetail)
    drawPath(path = bowPath, color = strokeColor, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(color = strokeColor, radius = s * 0.18f, center = Offset(cx, cy))
}

private fun DrawScope.drawSealLily(cx: Float, cy: Float, s: Float, strokeColor: Color) {
    for (i in 0 until 5) {
        val angle = (i * (2 * Math.PI / 5) - Math.PI / 2).toFloat()
        val petalPath = Path().apply {
            moveTo(cx, cy)
            val tipX = cx + s * 0.9f * cos(angle)
            val tipY = cy + s * 0.9f * sin(angle)
            val leftAngle = angle - 0.3f
            val rightAngle = angle + 0.3f
            val ctrl1X = cx + s * 0.6f * cos(leftAngle)
            val ctrl1Y = cy + s * 0.6f * sin(leftAngle)
            val ctrl2X = cx + s * 0.6f * cos(rightAngle)
            val ctrl2Y = cy + s * 0.6f * sin(rightAngle)

            cubicTo(ctrl1X, ctrl1Y, tipX, tipY, tipX, tipY)
            cubicTo(ctrl2X, ctrl2Y, cx, cy, cx, cy)
            close()
        }
        drawPath(path = petalPath, color = Color(0x35FFFFFF))
        drawPath(path = petalPath, color = strokeColor, style = Stroke(width = 1.5.dp.toPx()))
    }
    // Yellow pistil center
    drawCircle(color = Color(0xFFFFD75E), radius = s * 0.22f, center = Offset(cx, cy))
    drawCircle(color = strokeColor, radius = s * 0.22f, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))
}

private fun DrawScope.drawSealTeddy(cx: Float, cy: Float, s: Float, strokeColor: Color) {
    // Left ear
    drawCircle(color = strokeColor, radius = s * 0.26f, center = Offset(cx - s * 0.62f, cy - s * 0.55f), style = Stroke(width = 1.6.dp.toPx()))
    // Right ear
    drawCircle(color = strokeColor, radius = s * 0.26f, center = Offset(cx + s * 0.62f, cy - s * 0.55f), style = Stroke(width = 1.6.dp.toPx()))
    // Head
    drawCircle(color = strokeColor, radius = s * 0.68f, center = Offset(cx, cy + s * 0.05f), style = Stroke(width = 1.8.dp.toPx()))
    // Eyes
    drawCircle(color = strokeColor, radius = s * 0.09f, center = Offset(cx - s * 0.26f, cy - s * 0.08f))
    drawCircle(color = strokeColor, radius = s * 0.09f, center = Offset(cx + s * 0.26f, cy - s * 0.08f))
    // Snout
    drawCircle(color = Color(0x33FFFFFF), radius = s * 0.28f, center = Offset(cx, cy + s * 0.26f))
    drawCircle(color = strokeColor, radius = s * 0.28f, center = Offset(cx, cy + s * 0.26f), style = Stroke(width = 1.4.dp.toPx()))
    // Nose
    drawCircle(color = strokeColor, radius = s * 0.10f, center = Offset(cx, cy + s * 0.18f))
}

private fun DrawScope.drawSealStar(cx: Float, cy: Float, s: Float, strokeColor: Color) {
    val starPath = Path()
    val points = 5
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) s else s * 0.45f
        val angle = (i * Math.PI / points - Math.PI / 2).toFloat()
        val px = cx + r * cos(angle)
        val py = cy + r * sin(angle)
        if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
    }
    starPath.close()
    drawPath(path = starPath, color = Color(0x33FFFFFF))
    drawPath(path = starPath, color = strokeColor, style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round))
}

private fun DrawScope.drawSealRoseCrest(cx: Float, cy: Float, s: Float, strokeColor: Color) {
    // Laurel branches left & right
    val laurelPath = Path().apply {
        // Left branch
        moveTo(cx - s * 0.15f, cy + s * 0.8f)
        cubicTo(cx - s * 0.7f, cy + s * 0.5f, cx - s * 0.7f, cy - s * 0.4f, cx - s * 0.1f, cy - s * 0.7f)
        // Right branch
        moveTo(cx + s * 0.15f, cy + s * 0.8f)
        cubicTo(cx + s * 0.7f, cy + s * 0.5f, cx + s * 0.7f, cy - s * 0.4f, cx + s * 0.1f, cy - s * 0.7f)
    }
    drawPath(path = laurelPath, color = strokeColor, style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round))
    // Center miniature rose blossom
    drawCircle(color = strokeColor, radius = s * 0.28f, center = Offset(cx, cy), style = Stroke(width = 1.5.dp.toPx()))
    drawCircle(color = strokeColor, radius = s * 0.14f, center = Offset(cx, cy), style = Stroke(width = 1.3.dp.toPx()))
}

private fun DrawScope.drawSealMonogram(cx: Float, cy: Float, s: Float, strokeColor: Color, char: String) {
    // Clean ornamental monogram circle
    drawCircle(color = strokeColor, radius = s * 0.85f, center = Offset(cx, cy), style = Stroke(width = 1.4.dp.toPx()))
    
    // Draw initial monogram lines manually for crisp canvas vector representation
    val ch = (char.firstOrNull() ?: 'S').uppercaseChar()
    val glyphPath = Path()
    when (ch) {
        'S' -> {
            glyphPath.moveTo(cx + s * 0.35f, cy - s * 0.45f)
            glyphPath.cubicTo(cx - s * 0.45f, cy - s * 0.55f, cx - s * 0.45f, cy - s * 0.05f, cx, cy)
            glyphPath.cubicTo(cx + s * 0.45f, cy + s * 0.05f, cx + s * 0.45f, cy + s * 0.55f, cx - s * 0.35f, cy + s * 0.45f)
        }
        'A' -> {
            glyphPath.moveTo(cx - s * 0.35f, cy + s * 0.5f)
            glyphPath.lineTo(cx, cy - s * 0.5f)
            glyphPath.lineTo(cx + s * 0.35f, cy + s * 0.5f)
            glyphPath.moveTo(cx - s * 0.2f, cy + s * 0.15f)
            glyphPath.lineTo(cx + s * 0.2f, cy + s * 0.15f)
        }
        'J' -> {
            glyphPath.moveTo(cx - s * 0.25f, cy - s * 0.5f)
            glyphPath.lineTo(cx + s * 0.2f, cy - s * 0.5f)
            glyphPath.moveTo(cx + s * 0.08f, cy - s * 0.5f)
            glyphPath.lineTo(cx + s * 0.08f, cy + s * 0.3f)
            glyphPath.cubicTo(cx + s * 0.08f, cy + s * 0.55f, cx - s * 0.35f, cy + s * 0.55f, cx - s * 0.35f, cy + s * 0.25f)
        }
        'M' -> {
            glyphPath.moveTo(cx - s * 0.4f, cy + s * 0.5f)
            glyphPath.lineTo(cx - s * 0.4f, cy - s * 0.5f)
            glyphPath.lineTo(cx, cy + s * 0.1f)
            glyphPath.lineTo(cx + s * 0.4f, cy - s * 0.5f)
            glyphPath.lineTo(cx + s * 0.4f, cy + s * 0.5f)
        }
        else -> {
            // General lettermark serif 'L'
            glyphPath.moveTo(cx - s * 0.25f, cy - s * 0.5f)
            glyphPath.lineTo(cx - s * 0.25f, cy + s * 0.5f)
            glyphPath.lineTo(cx + s * 0.35f, cy + s * 0.5f)
        }
    }
    drawPath(path = glyphPath, color = strokeColor, style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
}
