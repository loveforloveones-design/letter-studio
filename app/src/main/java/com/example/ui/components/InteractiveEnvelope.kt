package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LetterData
import com.example.ui.theme.JpAccentCoral
import com.example.ui.theme.JpAccentYellow
import com.example.ui.theme.JpInk
import com.example.ui.theme.JpInkMedium
import com.example.ui.theme.JpPaperCream
import com.example.ui.theme.JpPaperVintage
import kotlin.math.roundToInt

@Composable
fun InteractiveEnvelope(
    letter: LetterData,
    modifier: Modifier = Modifier,
    onOpenLetter: () -> Unit = {}
) {
    var isOpen by remember { mutableStateOf(false) }
    var showBackFlap by remember { mutableStateOf(true) }

    val slideOffset by animateFloatAsState(
        targetValue = if (isOpen) -140f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "envelope_slide"
    )

    val sealScale by animateFloatAsState(
        targetValue = if (isOpen) 0.85f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "seal_scale"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isOpen) "Your Letter is Unsealed!" else "Your Sealed Envelope",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = if (isOpen) "Letter ready for Sophie" else "Tap seal or envelope to open",
            fontSize = 13.sp,
            color = JpInkMedium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Envelope container
        Box(
            modifier = Modifier
                .width(320.dp)
                .height(230.dp),
            contentAlignment = Alignment.Center
        ) {
            // Letter sliding out behind front pocket
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, slideOffset.roundToInt()) }
                    .width(270.dp)
                    .height(180.dp)
                    .shadow(elevation = if (isOpen) 8.dp else 2.dp, shape = RoundedCornerShape(8.dp))
                    .background(letter.paperTheme.paperColor, RoundedCornerShape(8.dp))
                    .border(1.5.dp, JpInk, RoundedCornerShape(8.dp))
                    .padding(14.dp)
                    .clickable {
                        onOpenLetter()
                    }
            ) {
                Column {
                    Text(
                        text = letter.salutation.ifBlank { "Dear ${letter.recipient}," },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = JpInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = letter.bodyText.take(120) + "...",
                        fontSize = 9.sp,
                        lineHeight = 13.sp,
                        color = JpInk,
                        fontFamily = FontFamily.Cursive
                    )
                }
            }

            // Envelope body canvas (pocket & flap)
            Box(
                modifier = Modifier
                    .width(310.dp)
                    .aspectRatio(200f / 136f)
                    .clickable {
                        isOpen = !isOpen
                    }
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val strokeW = 2.4.dp.toPx()

                    // Envelope background body
                    drawRoundRect(
                        color = JpPaperVintage,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
                    )
                    drawRoundRect(
                        color = JpInk,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                        style = Stroke(width = strokeW)
                    )

                    if (showBackFlap) {
                        // Flap diagonal lines meeting at center
                        val flapPath = Path().apply {
                            moveTo(6.dp.toPx(), 6.dp.toPx())
                            lineTo(w / 2f, h * 0.60f)
                            lineTo(w - 6.dp.toPx(), 6.dp.toPx())
                        }
                        drawPath(
                            path = flapPath,
                            color = JpInk,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Bottom pocket fold lines
                        val pocketPath = Path().apply {
                            moveTo(6.dp.toPx(), h - 6.dp.toPx())
                            lineTo(w / 2f, h * 0.52f)
                            lineTo(w - 6.dp.toPx(), h - 6.dp.toPx())
                        }
                        drawPath(
                            path = pocketPath,
                            color = Color(0x3333314E),
                            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // Envelope Front / Back content
                if (showBackFlap) {
                    // Centered Wax Seal at intersection of flap
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 12.dp)
                            .scale(sealScale)
                            .clickable {
                                isOpen = !isOpen
                            }
                    ) {
                        WaxSealBadge(
                            style = letter.sealStyle,
                            sealColor = letter.sealColor,
                            size = 64.dp,
                            monogramChar = letter.initialMonogram
                        )
                    }
                } else {
                    // Front of envelope: Postage stamp in top-right + "for [recipient]" in center
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp)
                            .rotate(-3f)
                    ) {
                        PostageStampGraphic(modifier = Modifier.size(46.dp))
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "for",
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontFamily = FontFamily.Serif,
                            color = JpInkMedium
                        )
                        Text(
                            text = letter.recipient.ifBlank { "Someone Special" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Cursive,
                            color = JpInk,
                            textAlign = TextAlign.Center
                        )
                        if (letter.recipientAddress.isNotBlank()) {
                            Text(
                                text = letter.recipientAddress,
                                fontSize = 10.sp,
                                color = JpInkMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Interactive control buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { isOpen = !isOpen },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOpen) JpAccentCoral else JpAccentYellow,
                    contentColor = JpInk
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, JpInk)
            ) {
                Icon(
                    imageVector = if (isOpen) Icons.Default.MarkEmailRead else Icons.Default.Drafts,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOpen) "Close Envelope" else "Open Envelope",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            OutlinedButton(
                onClick = { showBackFlap = !showBackFlap },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = JpPaperCream,
                    contentColor = JpInk
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, JpInk)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showBackFlap) "Flip to Front" else "Flip to Seal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
