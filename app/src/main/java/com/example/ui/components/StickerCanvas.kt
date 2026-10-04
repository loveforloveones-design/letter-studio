package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.LetterSticker
import com.example.model.StickerType
import com.example.ui.theme.JpAccentCoral
import com.example.ui.theme.JpAccentPeach
import com.example.ui.theme.JpAccentPink
import com.example.ui.theme.JpAccentYellow
import com.example.ui.theme.JpInk
import com.example.ui.theme.JpPaperCream
import com.example.ui.theme.JpPaperVintage
import com.example.ui.theme.JpPinkBg
import com.example.ui.theme.JpSealRed
import kotlin.math.roundToInt

@Composable
fun LetterStickerItem(
    sticker: LetterSticker,
    paperWidthPx: Float,
    paperHeightPx: Float,
    onMove: (newX: Float, newY: Float) -> Unit,
    onRemove: () -> Unit,
    interactive: Boolean = true
) {
    var isSelected by remember { mutableStateOf(false) }

    val stickerSize = (64 * sticker.scale).dp
    val targetXPx = (sticker.posX * paperWidthPx) - (stickerSize.value * 1.5f)
    val targetYPx = (sticker.posY * paperHeightPx) - (stickerSize.value * 1.5f)

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = targetXPx.coerceAtLeast(0f).roundToInt(),
                    y = targetYPx.coerceAtLeast(0f).roundToInt()
                )
            }
            .rotate(sticker.rotation)
            .then(
                if (interactive) {
                    Modifier
                        .clickable { isSelected = !isSelected }
                        .pointerInput(sticker.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                if (paperWidthPx > 0 && paperHeightPx > 0) {
                                    val newX = (sticker.posX + (dragAmount.x / paperWidthPx)).coerceIn(0.05f, 0.95f)
                                    val newY = (sticker.posY + (dragAmount.y / paperHeightPx)).coerceIn(0.05f, 0.95f)
                                    onMove(newX, newY)
                                }
                            }
                        }
                } else Modifier
            )
    ) {
        // Draw the specific sticker graphic
        Box(
            modifier = Modifier
                .size(stickerSize)
                .then(
                    if (isSelected && interactive) {
                        Modifier.border(1.5.dp, JpAccentCoral, RoundedCornerShape(8.dp))
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            when (sticker.type) {
                StickerType.POSTAGE_STAMP -> PostageStampGraphic(modifier = Modifier.size(stickerSize))
                StickerType.BOW -> RibbonBowGraphic(modifier = Modifier.size(stickerSize))
                StickerType.LILY -> LilyFlowerGraphic(modifier = Modifier.size(stickerSize))
                StickerType.WAX_HEART -> WaxHeartGraphic(modifier = Modifier.size(stickerSize))
                StickerType.COFFEE -> CoffeeCupGraphic(modifier = Modifier.size(stickerSize))
                StickerType.STAR -> TwinkleStarGraphic(modifier = Modifier.size(stickerSize))
                StickerType.TEDDY -> TeddyGraphic(modifier = Modifier.size(stickerSize))
                StickerType.BOTANICAL -> BotanicalFernGraphic(modifier = Modifier.size(stickerSize))
                StickerType.WASHI_TAPE -> WashiTapeGraphic(modifier = Modifier.size(stickerSize))
            }
        }

        // Close/delete button if selected in interactive mode
        if (isSelected && interactive) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(20.dp)
                    .background(JpInk, CircleShape)
                    .clickable { onRemove() }
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove sticker",
                    tint = JpPaperCream,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun PostageStampGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height * 1.15f
        val strokeW = 1.8.dp.toPx()

        // Stamp base card
        drawRoundRect(
            color = JpPaperVintage,
            size = Size(w, h),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        // Perforated edge lines (dash border)
        drawRoundRect(
            color = JpInk,
            size = Size(w, h),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = strokeW)
        )

        // Inner frame
        val inset = 6.dp.toPx()
        drawRect(
            color = JpPinkBg,
            topLeft = Offset(inset, inset),
            size = Size(w - inset * 2, h - inset * 2)
        )
        drawRect(
            color = JpInk,
            topLeft = Offset(inset, inset),
            size = Size(w - inset * 2, h - inset * 2),
            style = Stroke(width = 1.2.dp.toPx())
        )

        // Center silhouette (little bird / flower)
        val cx = w / 2f
        val cy = h / 2f
        drawCircle(color = JpAccentPink, radius = 9.dp.toPx(), center = Offset(cx, cy))
        drawCircle(color = JpInk, radius = 9.dp.toPx(), center = Offset(cx, cy), style = Stroke(width = 1.4.dp.toPx()))

        // Wavy postal cancellation postmark lines across
        val wavePath = Path().apply {
            moveTo(cx + 8.dp.toPx(), cy - 14.dp.toPx())
            cubicTo(cx + 18.dp.toPx(), cy - 20.dp.toPx(), cx + 24.dp.toPx(), cy - 10.dp.toPx(), cx + 34.dp.toPx(), cy - 16.dp.toPx())
            moveTo(cx + 6.dp.toPx(), cy - 6.dp.toPx())
            cubicTo(cx + 16.dp.toPx(), cy - 12.dp.toPx(), cx + 22.dp.toPx(), cy - 2.dp.toPx(), cx + 32.dp.toPx(), cy - 8.dp.toPx())
        }
        drawPath(path = wavePath, color = Color(0x9933314E), style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun RibbonBowGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val s = size.minDimension * 0.42f
        val strokeW = 1.8.dp.toPx()

        val bow = Path().apply {
            // Left wing
            moveTo(cx, cy)
            cubicTo(cx - s * 1.1f, cy - s * 0.9f, cx - s * 1.3f, cy + s * 0.2f, cx, cy)
            // Right wing
            cubicTo(cx + s * 1.1f, cy - s * 0.9f, cx + s * 1.3f, cy + s * 0.2f, cx, cy)
            // Ribbons tails
            moveTo(cx - s * 0.2f, cy + s * 0.2f)
            lineTo(cx - s * 0.7f, cy + s * 1.1f)
            lineTo(cx - s * 0.4f, cy + s * 0.95f)
            lineTo(cx - s * 0.1f, cy + s * 0.3f)

            moveTo(cx + s * 0.2f, cy + s * 0.2f)
            lineTo(cx + s * 0.7f, cy + s * 1.1f)
            lineTo(cx + s * 0.4f, cy + s * 0.95f)
            lineTo(cx + s * 0.1f, cy + s * 0.3f)
        }
        drawPath(path = bow, color = JpAccentPink)
        drawPath(path = bow, color = JpInk, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color = JpAccentCoral, radius = s * 0.28f, center = Offset(cx, cy))
        drawCircle(color = JpInk, radius = s * 0.28f, center = Offset(cx, cy), style = Stroke(width = strokeW))
    }
}

@Composable
fun LilyFlowerGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val s = size.minDimension * 0.40f

        for (i in 0 until 5) {
            val angle = (i * (2 * Math.PI / 5) - Math.PI / 2).toFloat()
            val petal = Path().apply {
                moveTo(cx, cy)
                val tipX = cx + s * kotlin.math.cos(angle)
                val tipY = cy + s * kotlin.math.sin(angle)
                val a1 = angle - 0.35f
                val a2 = angle + 0.35f
                cubicTo(
                    cx + s * 0.6f * kotlin.math.cos(a1), cy + s * 0.6f * kotlin.math.sin(a1),
                    tipX, tipY,
                    tipX, tipY
                )
                cubicTo(
                    cx + s * 0.6f * kotlin.math.cos(a2), cy + s * 0.6f * kotlin.math.sin(a2),
                    cx, cy,
                    cx, cy
                )
                close()
            }
            drawPath(path = petal, color = Color(0xFFFBE4EE))
            drawPath(path = petal, color = JpInk, style = Stroke(width = 1.6.dp.toPx()))
        }
        drawCircle(color = JpAccentYellow, radius = s * 0.26f, center = Offset(cx, cy))
        drawCircle(color = JpInk, radius = s * 0.26f, center = Offset(cx, cy), style = Stroke(width = 1.6.dp.toPx()))
    }
}

@Composable
fun WaxHeartGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val s = size.minDimension * 0.38f

        val heart = Path().apply {
            moveTo(cx, cy + s * 0.9f)
            cubicTo(cx - s * 1.1f, cy + s * 0.3f, cx - s * 1.1f, cy - s * 0.8f, cx - s * 0.45f, cy - s * 0.8f)
            cubicTo(cx - s * 0.1f, cy - s * 0.8f, cx, cy - s * 0.4f, cx, cy - s * 0.4f)
            cubicTo(cx, cy - s * 0.4f, cx + s * 0.1f, cy - s * 0.8f, cx + s * 0.45f, cy - s * 0.8f)
            cubicTo(cx + s * 1.1f, cy - s * 0.8f, cx + s * 1.1f, cy + s * 0.3f, cx, cy + s * 0.9f)
            close()
        }
        drawPath(path = heart, color = JpSealRed)
        drawPath(path = heart, color = JpInk, style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round))
    }
}

@Composable
fun CoffeeCupGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f + 4.dp.toPx()
        val w = 18.dp.toPx()
        val h = 18.dp.toPx()

        // Cup body
        val cup = Path().apply {
            moveTo(cx - w, cy - h / 2)
            lineTo(cx + w, cy - h / 2)
            lineTo(cx + w * 0.78f, cy + h / 2)
            lineTo(cx - w * 0.78f, cy + h / 2)
            close()
        }
        drawPath(path = cup, color = JpPaperCream)
        drawPath(path = cup, color = JpInk, style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round))

        // Handle
        val handle = Path().apply {
            moveTo(cx + w * 0.9f, cy - h * 0.3f)
            cubicTo(cx + w * 1.6f, cy - h * 0.3f, cx + w * 1.6f, cy + h * 0.3f, cx + w * 0.75f, cy + h * 0.3f)
        }
        drawPath(path = handle, color = JpInk, style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round))

        // Steam swirls
        val steam = Path().apply {
            moveTo(cx - 5.dp.toPx(), cy - h / 2 - 4.dp.toPx())
            cubicTo(cx - 9.dp.toPx(), cy - h / 2 - 10.dp.toPx(), cx - 2.dp.toPx(), cy - h / 2 - 14.dp.toPx(), cx - 6.dp.toPx(), cy - h / 2 - 20.dp.toPx())
            moveTo(cx + 5.dp.toPx(), cy - h / 2 - 4.dp.toPx())
            cubicTo(cx + 1.dp.toPx(), cy - h / 2 - 10.dp.toPx(), cx + 8.dp.toPx(), cy - h / 2 - 14.dp.toPx(), cx + 4.dp.toPx(), cy - h / 2 - 20.dp.toPx())
        }
        drawPath(path = steam, color = Color(0x8833314E), style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
fun TwinkleStarGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val s = size.minDimension * 0.40f

        val star = Path()
        val points = 5
        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) s else s * 0.48f
            val angle = (i * Math.PI / points - Math.PI / 2).toFloat()
            val px = cx + r * kotlin.math.cos(angle)
            val py = cy + r * kotlin.math.sin(angle)
            if (i == 0) star.moveTo(px, py) else star.lineTo(px, py)
        }
        star.close()
        drawPath(path = star, color = JpAccentYellow)
        drawPath(path = star, color = JpInk, style = Stroke(width = 1.8.dp.toPx(), join = StrokeJoin.Round))
    }
}

@Composable
fun TeddyGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val s = size.minDimension * 0.36f

        // Ears
        drawCircle(color = JpAccentPeach, radius = s * 0.34f, center = Offset(cx - s * 0.65f, cy - s * 0.6f))
        drawCircle(color = JpInk, radius = s * 0.34f, center = Offset(cx - s * 0.65f, cy - s * 0.6f), style = Stroke(width = 1.8.dp.toPx()))
        drawCircle(color = JpAccentPeach, radius = s * 0.34f, center = Offset(cx + s * 0.65f, cy - s * 0.6f))
        drawCircle(color = JpInk, radius = s * 0.34f, center = Offset(cx + s * 0.65f, cy - s * 0.6f), style = Stroke(width = 1.8.dp.toPx()))

        // Head
        drawCircle(color = JpAccentPeach, radius = s * 0.82f, center = Offset(cx, cy + s * 0.1f))
        drawCircle(color = JpInk, radius = s * 0.82f, center = Offset(cx, cy + s * 0.1f), style = Stroke(width = 2.0.dp.toPx()))

        // Eyes
        drawCircle(color = JpInk, radius = s * 0.11f, center = Offset(cx - s * 0.3f, cy - s * 0.05f))
        drawCircle(color = JpInk, radius = s * 0.11f, center = Offset(cx + s * 0.3f, cy - s * 0.05f))

        // Muzzle
        drawCircle(color = JpPaperCream, radius = s * 0.34f, center = Offset(cx, cy + s * 0.32f))
        drawCircle(color = JpInk, radius = s * 0.34f, center = Offset(cx, cy + s * 0.32f), style = Stroke(width = 1.5.dp.toPx()))
        drawCircle(color = JpInk, radius = s * 0.12f, center = Offset(cx, cy + s * 0.22f))
    }
}

@Composable
fun BotanicalFernGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val stem = Path().apply {
            moveTo(cx - 10.dp.toPx(), cy + 18.dp.toPx())
            cubicTo(cx - 2.dp.toPx(), cy + 5.dp.toPx(), cx + 4.dp.toPx(), cy - 5.dp.toPx(), cx + 12.dp.toPx(), cy - 18.dp.toPx())
        }
        drawPath(path = stem, color = JpInk, style = Stroke(width = 2.0.dp.toPx(), cap = StrokeCap.Round))

        // Leaflets
        for (i in -2..2) {
            val offset = i * 6.dp.toPx()
            drawCircle(
                color = Color(0xFF789D7A),
                radius = 4.dp.toPx(),
                center = Offset(cx + offset * 0.8f - 6.dp.toPx(), cy + offset)
            )
            drawCircle(
                color = Color(0xFF789D7A),
                radius = 4.dp.toPx(),
                center = Offset(cx + offset * 0.8f + 6.dp.toPx(), cy + offset - 4.dp.toPx())
            )
        }
    }
}

@Composable
fun WashiTapeGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width * 1.1f
        val h = size.height * 0.38f
        val tape = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            // Torn edge right
            lineTo(w - 3.dp.toPx(), h * 0.5f)
            lineTo(w, h)
            lineTo(0f, h)
            // Torn edge left
            lineTo(3.dp.toPx(), h * 0.5f)
            close()
        }
        drawPath(path = tape, color = Color(0xCCFFD75E))
        drawPath(path = tape, color = Color(0x3333314E), style = Stroke(width = 1.0.dp.toPx()))
    }
}
