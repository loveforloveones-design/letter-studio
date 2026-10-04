package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FontChoice
import com.example.model.HeaderStyle
import com.example.model.LetterData
import com.example.model.LetterSticker
import com.example.ui.theme.JpAccentCoral
import com.example.ui.theme.JpAccentYellow
import com.example.ui.theme.JpInk
import com.example.ui.theme.JpInkMedium
import com.example.ui.theme.JpInkMuted
import com.example.ui.theme.JpPaperCream
import com.example.ui.theme.JpPaperVintage

@Composable
fun PaperCanvas(
    letter: LetterData,
    modifier: Modifier = Modifier,
    onMoveSticker: ((id: String, x: Float, y: Float) -> Unit)? = null,
    onRemoveSticker: ((id: String) -> Unit)? = null,
    interactiveStickers: Boolean = true
) {
    val bodyFontFamily = when (letter.fontChoice) {
        FontChoice.HANDWRITTEN -> FontFamily.Cursive
        FontChoice.SERIF_CLASSIC -> FontFamily.Serif
        FontChoice.MONO_TYPEWRITER -> FontFamily.Monospace
        FontChoice.SANS_CLEAN -> FontFamily.SansSerif
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x3333314E)
            )
            .background(letter.paperTheme.paperColor, RoundedCornerShape(16.dp))
            .border(2.dp, JpInk, RoundedCornerShape(16.dp))
    ) {
        val paperWidthPx = constraints.maxWidth.toFloat()
        val paperHeightPx = constraints.maxHeight.toFloat()

        // Background ruled lines (if enabled)
        if (letter.showRuledLines) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val lineSpacing = 32.dp.toPx()
                var y = 60.dp.toPx()
                while (y < size.height - 20.dp.toPx()) {
                    drawLine(
                        color = letter.paperTheme.lineColor,
                        start = Offset(20.dp.toPx(), y),
                        end = Offset(size.width - 20.dp.toPx(), y),
                        strokeWidth = 1.0.dp.toPx()
                    )
                    y += lineSpacing
                }
            }
        }

        // Letter content column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Render specific Header / Letterhead
            when (letter.headerStyle) {
                HeaderStyle.ROMANTIC_PERSONAL -> RomanticPersonalHeader(letter)
                HeaderStyle.CORPORATE_OFFICIAL -> CorporateOfficialHeader(letter)
                HeaderStyle.MINIMAL_MODERN -> MinimalModernHeader(letter)
                HeaderStyle.VINTAGE_CREST -> VintageCrestHeader(letter)
                HeaderStyle.ARTISAN_BOTANICAL -> ArtisanBotanicalHeader(letter)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Subject Line (optional)
            if (letter.subject.isNotBlank()) {
                Text(
                    text = letter.subject,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = bodyFontFamily,
                    color = JpInk,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Salutation
            Text(
                text = letter.salutation.ifBlank { "Dear ${letter.recipient}," },
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = bodyFontFamily,
                color = JpInk,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Letter Body
            Text(
                text = letter.bodyText,
                fontSize = 15.sp,
                lineHeight = 25.sp,
                fontFamily = bodyFontFamily,
                color = JpInk,
                modifier = Modifier.fillMaxWidth()
            )

            // Polaroid Attachment (if enabled)
            if (letter.hasPolaroid) {
                Spacer(modifier = Modifier.height(20.dp))
                PolaroidCard(caption = letter.polaroidCaption, rotation = letter.polaroidRotation)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign-off and Signature Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = letter.signOff,
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = bodyFontFamily,
                    color = JpInk
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Handwritten Signature
                    Text(
                        text = letter.signatureText.ifBlank { letter.sender },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Cursive,
                        color = JpInk,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                    // Stamped Wax Seal in chosen style and wax color
                    WaxSealBadge(
                        style = letter.sealStyle,
                        sealColor = letter.sealColor,
                        size = 52.dp,
                        monogramChar = letter.initialMonogram
                    )
                }

                if (letter.senderTitle.isNotBlank() || letter.senderOrg.isNotBlank()) {
                    Text(
                        text = listOf(letter.senderTitle, letter.senderOrg).filter { it.isNotBlank() }.joinToString(" · "),
                        fontSize = 11.sp,
                        color = JpInkMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Draggable / Interactive Stickers layer
        letter.stickers.forEach { sticker ->
            LetterStickerItem(
                sticker = sticker,
                paperWidthPx = if (paperWidthPx > 0) paperWidthPx else 800f,
                paperHeightPx = if (paperHeightPx > 0) paperHeightPx else 1200f,
                onMove = { newX, newY -> onMoveSticker?.invoke(sticker.id, newX, newY) },
                onRemove = { onRemoveSticker?.invoke(sticker.id) },
                interactive = interactiveStickers
            )
        }
    }
}

@Composable
fun RomanticPersonalHeader(letter: LetterData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "💌 A Note for You",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = JpAccentCoral,
                letterSpacing = 1.sp
            )
            Text(
                text = "from ${letter.sender}",
                fontSize = 13.sp,
                color = JpInkMedium,
                fontFamily = FontFamily.Cursive
            )
        }

        Text(
            text = letter.date,
            fontSize = 12.sp,
            color = JpInkMuted,
            fontFamily = FontFamily.Default
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    HorizontalDivider(thickness = 1.dp, color = Color(0x3333314E))
}

@Composable
fun CorporateOfficialHeader(letter: LetterData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = letter.senderOrg.ifBlank { "VANGUARD INITIATIVE" }.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk,
                    letterSpacing = 1.5.sp
                )
                if (letter.senderTitle.isNotBlank()) {
                    Text(
                        text = letter.senderTitle,
                        fontSize = 12.sp,
                        color = JpInkMedium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = letter.date,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = JpInk
                )
                if (letter.referenceNumber.isNotBlank()) {
                    Text(
                        text = letter.referenceNumber,
                        fontSize = 10.sp,
                        color = JpInkMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(thickness = 2.dp, color = JpInk)
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 0.8.dp, color = Color(0x4433314E))

        // Contact info metadata row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = letter.senderAddress.ifBlank { "Studio Suite 3B, Innovation Blvd" },
                fontSize = 10.sp,
                color = JpInkMedium
            )
            Text(
                text = "${letter.senderEmail} · ${letter.senderPhone}",
                fontSize = 10.sp,
                color = JpInkMedium
            )
        }
        HorizontalDivider(thickness = 0.8.dp, color = Color(0x2233314E))
    }
}

@Composable
fun MinimalModernHeader(letter: LetterData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(JpInk, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.initialMonogram.ifBlank { "S" }.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpPaperCream
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = letter.sender.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk,
                    letterSpacing = 1.sp
                )
                Text(
                    text = letter.senderOrg.ifBlank { "Independent Studio" },
                    fontSize = 11.sp,
                    color = JpInkMuted
                )
            }
        }

        Text(
            text = letter.date,
            fontSize = 11.sp,
            color = JpInkMuted
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(thickness = 1.2.dp, color = JpInk)
}

@Composable
fun VintageCrestHeader(letter: LetterData) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✦   ${letter.senderOrg.ifBlank { "EX LIBRIS ET EPISTOLA" }.uppercase()}   ✦",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            letterSpacing = 2.sp,
            color = JpInk
        )
        Text(
            text = "— From the Desk of ${letter.sender} —",
            fontSize = 11.sp,
            fontStyle = FontStyle.Italic,
            fontFamily = FontFamily.Serif,
            color = JpInkMedium,
            modifier = Modifier.padding(top = 2.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Addressed to: ${letter.recipient}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                color = JpInkMedium
            )
            Text(
                text = letter.date,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                color = JpInkMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.dp, color = Color(0x5533314E))
    }
}

@Composable
fun ArtisanBotanicalHeader(letter: LetterData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🌿", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = letter.senderOrg.ifBlank { "The Artisan Press" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = JpInk
                )
                Text(
                    text = "Crafted with care · ${letter.sender}",
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = JpInkMuted
                )
            }
        }

        Text(
            text = letter.date,
            fontSize = 12.sp,
            fontFamily = FontFamily.Serif,
            color = JpInk
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    HorizontalDivider(thickness = 1.2.dp, color = Color(0x3333314E))
}

@Composable
fun PolaroidCard(caption: String, rotation: Float = -3f) {
    Box(
        modifier = Modifier
            .rotate(rotation)
            .shadow(4.dp, RoundedCornerShape(4.dp))
            .background(JpPaperCream, RoundedCornerShape(4.dp))
            .border(1.5.dp, JpInk, RoundedCornerShape(4.dp))
            .padding(8.dp)
            .width(140.dp)
    ) {
        // Washi tape at top
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .width(42.dp)
                .height(12.dp)
                .background(JpAccentYellow.copy(alpha = 0.85f), RoundedCornerShape(2.dp))
                .border(0.8.dp, JpInk.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Photo square with nostalgic illustration
            Box(
                modifier = Modifier
                    .size(124.dp)
                    .background(Color(0xFFEFE8DA), RoundedCornerShape(2.dp))
                    .border(1.dp, Color(0x2233314E)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📷", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = caption.ifBlank { "memory" },
                fontSize = 11.sp,
                fontFamily = FontFamily.Cursive,
                color = JpInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
