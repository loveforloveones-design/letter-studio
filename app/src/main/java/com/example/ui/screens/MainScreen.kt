package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FontChoice
import com.example.model.HeaderStyle
import com.example.model.LetterData
import com.example.model.LetterSticker
import com.example.model.PaperTheme
import com.example.model.SealColor
import com.example.model.SealStyle
import com.example.model.StickerType
import com.example.ui.components.InteractiveEnvelope
import com.example.ui.components.PaperCanvas
import com.example.ui.components.WaxSealBadge
import com.example.ui.theme.JpAccentCoral
import com.example.ui.theme.JpAccentPink
import com.example.ui.theme.JpAccentYellow
import com.example.ui.theme.JpInk
import com.example.ui.theme.JpInkFaint
import com.example.ui.theme.JpInkMedium
import com.example.ui.theme.JpInkMuted
import com.example.ui.theme.JpPaperCream
import com.example.ui.theme.JpPaperVintage
import com.example.ui.theme.JpPinkBg
import com.example.util.PdfExporter
import java.io.File

enum class ScreenTab(val title: String) {
    WRITE("Write"),
    STYLE("Letterhead"),
    SEAL("Wax Seal"),
    STICKERS("Stickers"),
    ENVELOPE("Envelope"),
    EXPORT("Export PDF")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var letter by remember { mutableStateOf(LetterData()) }
    var selectedTab by remember { mutableStateOf(ScreenTab.WRITE) }
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    var isGeneratingPdf by remember { mutableStateOf(false) }
    var showTemplateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WaxSealBadge(
                            style = letter.sealStyle,
                            sealColor = letter.sealColor,
                            size = 28.dp,
                            monogramChar = letter.initialMonogram
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Letter Studio",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = JpInk
                        )
                    }
                },
                actions = {
                    // Templates quick button
                    OutlinedButton(
                        onClick = { showTemplateDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = JpPaperCream,
                            contentColor = JpInk
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, JpInk),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = JpAccentCoral
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Templates",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Direct PDF export quick button
                    IconButton(
                        onClick = {
                            selectedTab = ScreenTab.EXPORT
                            val file = PdfExporter.generatePdf(context, letter)
                            generatedPdfFile = file
                            if (file != null) {
                                Toast.makeText(context, "PDF generated successfully!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Export PDF",
                            tint = JpInk
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = JpPinkBg
                )
            )
        },
        containerColor = JpPinkBg
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 840.dp

            if (isWideScreen) {
                // Tablet / Landscape Split View: Left Controls, Right Live Paper Canvas
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left: Tabs & Form controls
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                    ) {
                        TabsSelector(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            TabContent(
                                selectedTab = selectedTab,
                                letter = letter,
                                onLetterChange = { letter = it },
                                onSwitchTab = { selectedTab = it },
                                generatedPdfFile = generatedPdfFile,
                                onGeneratePdf = {
                                    isGeneratingPdf = true
                                    val f = PdfExporter.generatePdf(context, letter)
                                    generatedPdfFile = f
                                    isGeneratingPdf = false
                                }
                            )
                        }
                    }

                    // Right: Live Paper Canvas Preview
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column {
                            Text(
                                text = "Live Document Preview",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = JpInkMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            PaperCanvas(
                                letter = letter,
                                onMoveSticker = { id, x, y ->
                                    letter = letter.copy(
                                        stickers = letter.stickers.map {
                                            if (it.id == id) it.copy(posX = x, posY = y) else it
                                        }
                                    )
                                },
                                onRemoveSticker = { id ->
                                    letter = letter.copy(
                                        stickers = letter.stickers.filterNot { it.id == id }
                                    )
                                },
                                interactiveStickers = true
                            )
                        }
                    }
                }
            } else {
                // Portrait Mobile: Tab selector on top, content scrolling below
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    TabsSelector(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        TabContent(
                            selectedTab = selectedTab,
                            letter = letter,
                            onLetterChange = { letter = it },
                            onSwitchTab = { selectedTab = it },
                            generatedPdfFile = generatedPdfFile,
                            onGeneratePdf = {
                                isGeneratingPdf = true
                                val f = PdfExporter.generatePdf(context, letter)
                                generatedPdfFile = f
                                isGeneratingPdf = false
                            }
                        )
                    }
                }
            }
        }
    }

    // Template Picker Modal
    if (showTemplateDialog) {
        TemplateSelectionDialog(
            onDismiss = { showTemplateDialog = false },
            onSelectTemplate = { selected ->
                letter = selected
                showTemplateDialog = false
                Toast.makeText(context, "Template applied!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun TabsSelector(
    selectedTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScreenTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .shadow(if (isSelected) 3.dp else 0.dp, RoundedCornerShape(12.dp))
                    .background(
                        color = if (isSelected) JpAccentYellow else JpPaperCream,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.8.dp,
                        color = JpInk,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    text = tab.title,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    color = JpInk
                )
            }
        }
    }
}

@Composable
fun TabContent(
    selectedTab: ScreenTab,
    letter: LetterData,
    onLetterChange: (LetterData) -> Unit,
    onSwitchTab: (ScreenTab) -> Unit,
    generatedPdfFile: File?,
    onGeneratePdf: () -> Unit
) {
    when (selectedTab) {
        ScreenTab.WRITE -> WriteTab(letter, onLetterChange, onContinue = { onSwitchTab(ScreenTab.STYLE) })
        ScreenTab.STYLE -> StyleTab(letter, onLetterChange, onContinue = { onSwitchTab(ScreenTab.SEAL) })
        ScreenTab.SEAL -> SealTab(letter, onLetterChange, onContinue = { onSwitchTab(ScreenTab.STICKERS) })
        ScreenTab.STICKERS -> StickersTab(letter, onLetterChange, onContinue = { onSwitchTab(ScreenTab.EXPORT) })
        ScreenTab.ENVELOPE -> EnvelopeTab(letter, onOpenLetter = { onSwitchTab(ScreenTab.EXPORT) })
        ScreenTab.EXPORT -> ExportTab(letter, generatedPdfFile, onGeneratePdf)
    }
}

// ======================== TAB 1: WRITE ========================
@Composable
fun WriteTab(
    letter: LetterData,
    onLetterChange: (LetterData) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = JpPaperCream),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.8.dp, JpInk)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Who's this for?",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk
                )
                Spacer(modifier = Modifier.height(8.dp))
                StyledInputField(
                    value = letter.recipient,
                    onValueChange = { onLetterChange(letter.copy(recipient = it)) },
                    placeholder = "Recipient name (e.g. Sophie, Acme Corp)"
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Greeting / Salutation",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = JpInkMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                StyledInputField(
                    value = letter.salutation,
                    onValueChange = { onLetterChange(letter.copy(salutation = it)) },
                    placeholder = "Dearest Sophie, / Dear Mr. Davis,"
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = JpPaperCream),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.8.dp, JpInk)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Letter Subject (Optional)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk
                )
                Spacer(modifier = Modifier.height(6.dp))
                StyledInputField(
                    value = letter.subject,
                    onValueChange = { onLetterChange(letter.copy(subject = it)) },
                    placeholder = "e.g. A small note of appreciation"
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Letter Body",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk
                )
                Spacer(modifier = Modifier.height(8.dp))
                StyledInputField(
                    value = letter.bodyText,
                    onValueChange = { onLetterChange(letter.copy(bodyText = it)) },
                    placeholder = "Write your heartfelt thoughts or letterhead body here...",
                    minLines = 7
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = JpPaperCream),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.8.dp, JpInk)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Sign-off & Signature",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk
                )
                Spacer(modifier = Modifier.height(8.dp))
                StyledInputField(
                    value = letter.signOff,
                    onValueChange = { onLetterChange(letter.copy(signOff = it)) },
                    placeholder = "With all my love, / Sincerely,"
                )

                Spacer(modifier = Modifier.height(10.dp))

                StyledInputField(
                    value = letter.signatureText,
                    onValueChange = { onLetterChange(letter.copy(signatureText = it)) },
                    placeholder = "Your name for cursive signature (e.g. Sabi)"
                )
            }
        }

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(
                containerColor = JpAccentCoral,
                contentColor = JpInk
            ),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, JpInk),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Next: Choose Letterhead Style →",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ======================== TAB 2: STYLE ========================
@Composable
fun StyleTab(
    letter: LetterData,
    onLetterChange: (LetterData) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Layout Style
        Text(
            text = "Header & Letterhead Style",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            HeaderStyle.entries.forEach { style ->
                val isSelected = letter.headerStyle == style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isSelected) 3.dp else 0.dp, RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) JpAccentYellow else JpPaperCream,
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            if (isSelected) 2.dp else 1.5.dp,
                            JpInk,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onLetterChange(letter.copy(headerStyle = style)) }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = style.displayName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = JpInk
                        )
                        Text(
                            text = style.description,
                            fontSize = 12.sp,
                            color = JpInkMedium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Sender & Organization Info
        Card(
            colors = CardDefaults.cardColors(containerColor = JpPaperCream),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.8.dp, JpInk)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Letterhead Sender & Organization",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk
                )
                StyledInputField(
                    value = letter.sender,
                    onValueChange = { onLetterChange(letter.copy(sender = it)) },
                    placeholder = "Sender Name (e.g. Sabi)"
                )
                StyledInputField(
                    value = letter.senderOrg,
                    onValueChange = { onLetterChange(letter.copy(senderOrg = it)) },
                    placeholder = "Organization / Studio (e.g. Janapa Studio)"
                )
                StyledInputField(
                    value = letter.senderTitle,
                    onValueChange = { onLetterChange(letter.copy(senderTitle = it)) },
                    placeholder = "Title / Role (e.g. Creative Director)"
                )
                StyledInputField(
                    value = letter.date,
                    onValueChange = { onLetterChange(letter.copy(date = it)) },
                    placeholder = "Date (e.g. October 3, 2026)"
                )
                StyledInputField(
                    value = letter.senderEmail,
                    onValueChange = { onLetterChange(letter.copy(senderEmail = it)) },
                    placeholder = "Contact Email"
                )
                StyledInputField(
                    value = letter.senderPhone,
                    onValueChange = { onLetterChange(letter.copy(senderPhone = it)) },
                    placeholder = "Contact Phone"
                )
            }
        }

        // Paper Theme Picker
        Text(
            text = "Stationery Paper Color",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PaperTheme.entries.forEach { theme ->
                val isSelected = letter.paperTheme == theme
                Box(
                    modifier = Modifier
                        .size(width = 110.dp, height = 75.dp)
                        .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(12.dp))
                        .background(theme.paperColor, RoundedCornerShape(12.dp))
                        .border(
                            if (isSelected) 2.4.dp else 1.5.dp,
                            if (isSelected) JpAccentCoral else JpInk,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onLetterChange(letter.copy(paperTheme = theme)) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = theme.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = JpInk
                    )
                }
            }
        }

        // Typography Selection
        Text(
            text = "Handwriting & Typography",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FontChoice.entries.forEach { font ->
                val isSelected = letter.fontChoice == font
                Box(
                    modifier = Modifier
                        .width(135.dp)
                        .background(
                            if (isSelected) JpAccentYellow else JpPaperCream,
                            RoundedCornerShape(12.dp)
                        )
                        .border(1.8.dp, JpInk, RoundedCornerShape(12.dp))
                        .clickable { onLetterChange(letter.copy(fontChoice = font)) }
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = font.displayName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = JpInk
                        )
                        Text(
                            text = font.previewName,
                            fontSize = 11.sp,
                            color = JpInkMedium
                        )
                    }
                }
            }
        }

        // Ruled Lines Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JpPaperCream, RoundedCornerShape(14.dp))
                .border(1.8.dp, JpInk, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notebook Ruled Lines",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk
                )
                Text(
                    text = "Classic stationery writing ruling",
                    fontSize = 11.sp,
                    color = JpInkMedium
                )
            }
            Switch(
                checked = letter.showRuledLines,
                onCheckedChange = { onLetterChange(letter.copy(showRuledLines = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = JpPaperCream,
                    checkedTrackColor = JpInk,
                    uncheckedThumbColor = JpInkMedium,
                    uncheckedTrackColor = JpInkFaint
                )
            )
        }

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = JpAccentCoral, contentColor = JpInk),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, JpInk),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Next: Customize Wax Seal →",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ======================== TAB 3: WAX SEAL ========================
@Composable
fun SealTab(
    letter: LetterData,
    onLetterChange: (LetterData) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Wax Seal Style Showcase
        Card(
            colors = CardDefaults.cardColors(containerColor = JpPaperCream),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.8.dp, JpInk)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Wax Seal Stamp",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = JpInk
                )
                Spacer(modifier = Modifier.height(16.dp))

                WaxSealBadge(
                    style = letter.sealStyle,
                    sealColor = letter.sealColor,
                    size = 110.dp,
                    monogramChar = letter.initialMonogram
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${letter.sealStyle.displayName} · ${letter.sealColor.displayName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = JpInkMedium
                )
            }
        }

        // Seal Emblem Selection
        Text(
            text = "Select Stamp Emblem",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SealStyle.entries.forEach { style ->
                val isSelected = letter.sealStyle == style
                Box(
                    modifier = Modifier
                        .size(width = 85.dp, height = 95.dp)
                        .background(
                            if (isSelected) JpAccentYellow else JpPaperCream,
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            if (isSelected) 2.2.dp else 1.5.dp,
                            if (isSelected) JpAccentCoral else JpInk,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onLetterChange(letter.copy(sealStyle = style)) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        WaxSealBadge(
                            style = style,
                            sealColor = letter.sealColor,
                            size = 46.dp,
                            monogramChar = letter.initialMonogram
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = style.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = JpInk
                        )
                    }
                }
            }
        }

        // Monogram Initial input if Monogram seal is selected
        if (letter.sealStyle == SealStyle.MONOGRAM) {
            Card(
                colors = CardDefaults.cardColors(containerColor = JpPaperCream),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, JpInk)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Monogram Letter Initial",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = JpInk
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    StyledInputField(
                        value = letter.initialMonogram,
                        onValueChange = { onLetterChange(letter.copy(initialMonogram = it.take(1))) },
                        placeholder = "Initial (e.g. S)"
                    )
                }
            }
        }

        // Seal Wax Color Palette
        Text(
            text = "Wax Seal Color",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SealColor.entries.forEach { col ->
                val isSelected = letter.sealColor == col
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(if (isSelected) 4.dp else 1.dp, CircleShape)
                        .background(col.color, CircleShape)
                        .border(
                            if (isSelected) 3.dp else 1.5.dp,
                            if (isSelected) JpInk else Color(0x5533314E),
                            CircleShape
                        )
                        .clickable { onLetterChange(letter.copy(sealColor = col)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(JpPaperCream, CircleShape)
                        )
                    }
                }
            }
        }

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = JpAccentCoral, contentColor = JpInk),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, JpInk),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Next: Add Stickers & Photo →",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ======================== TAB 4: STICKERS & PHOTO ========================
@Composable
fun StickersTab(
    letter: LetterData,
    onLetterChange: (LetterData) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Decorative Stamps & Stickers",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk
        )
        Text(
            text = "Tap any sticker below to place it onto your letterhead document. Drag to position anywhere!",
            fontSize = 13.sp,
            color = JpInkMedium
        )

        // Sticker Tray buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StickerType.entries.forEach { stickerType ->
                Box(
                    modifier = Modifier
                        .size(width = 80.dp, height = 75.dp)
                        .background(JpPaperCream, RoundedCornerShape(12.dp))
                        .border(1.5.dp, JpInk, RoundedCornerShape(12.dp))
                        .clickable {
                            // Add sticker at random / center spot
                            val newSticker = LetterSticker(
                                type = stickerType,
                                posX = (0.25f + (Math.random() * 0.5f)).toFloat(),
                                posY = (0.3f + (Math.random() * 0.4f)).toFloat(),
                                rotation = (-10f + (Math.random() * 20f)).toFloat()
                            )
                            onLetterChange(letter.copy(stickers = letter.stickers + newSticker))
                        }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stickerType.emoji, fontSize = 26.sp)
                        Text(
                            text = stickerType.label,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = JpInk
                        )
                    }
                }
            }
        }

        // Active stickers list with clear button
        if (letter.stickers.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${letter.stickers.size} stickers on document",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = JpInk
                )
                OutlinedButton(
                    onClick = { onLetterChange(letter.copy(stickers = emptyList())) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = JpInk),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JpInk)
                ) {
                    Text(text = "Clear All Stickers", fontSize = 11.sp)
                }
            }
        }

        // Polaroid Photo Card Option
        Card(
            colors = CardDefaults.cardColors(containerColor = JpPaperCream),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.8.dp, JpInk)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Polaroid Memory Card",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = JpInk
                        )
                        Text(
                            text = "Add a nostalgic photo card to your letter",
                            fontSize = 11.sp,
                            color = JpInkMedium
                        )
                    }
                    Switch(
                        checked = letter.hasPolaroid,
                        onCheckedChange = { onLetterChange(letter.copy(hasPolaroid = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JpPaperCream,
                            checkedTrackColor = JpInk
                        )
                    )
                }

                if (letter.hasPolaroid) {
                    Spacer(modifier = Modifier.height(10.dp))
                    StyledInputField(
                        value = letter.polaroidCaption,
                        onValueChange = { onLetterChange(letter.copy(polaroidCaption = it)) },
                        placeholder = "Polaroid caption (e.g. autumn in the park)"
                    )
                }
            }
        }

        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(containerColor = JpAccentCoral, contentColor = JpInk),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, JpInk),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Next: View Envelope & Export PDF →",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ======================== TAB 5: ENVELOPE ========================
@Composable
fun EnvelopeTab(letter: LetterData, onOpenLetter: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InteractiveEnvelope(letter = letter, onOpenLetter = onOpenLetter)
    }
}

// ======================== TAB 6: EXPORT PDF ========================
@Composable
fun ExportTab(
    letter: LetterData,
    generatedPdfFile: File?,
    onGeneratePdf: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Export & Print Letterhead Document",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk
        )
        Text(
            text = "Export your personalized stationery document into a crisp vector PDF (Standard A4 / US Letter), ready for printing, sharing, or digital archiving.",
            fontSize = 13.sp,
            color = JpInkMedium
        )

        // Actions grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val file = PdfExporter.generatePdf(context, letter)
                    if (file != null) {
                        onGeneratePdf()
                        PdfExporter.sharePdf(context, file)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = JpAccentYellow,
                    contentColor = JpInk
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, JpInk),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Share PDF", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    PdfExporter.printLetter(context, letter)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = JpAccentPink,
                    contentColor = JpInk
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, JpInk),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Direct Print", fontWeight = FontWeight.Bold)
            }
        }

        // View PDF Button
        Button(
            onClick = {
                val file = generatedPdfFile ?: PdfExporter.generatePdf(context, letter)
                if (file != null) {
                    PdfExporter.viewPdf(context, file)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = JpPaperCream,
                contentColor = JpInk
            ),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, JpInk),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Open in PDF Reader", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Full Interactive Paper Preview on this screen
        Text(
            text = "Full Document Preview",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = JpInk
        )

        PaperCanvas(
            letter = letter,
            interactiveStickers = false
        )
    }
}

// Reusable Styled Input Field with Janapasabi rough/bold border
@Composable
fun StyledInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder, color = JpInkMuted, fontSize = 14.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = JpPaperCream,
            unfocusedContainerColor = JpPaperCream,
            focusedBorderColor = JpInk,
            unfocusedBorderColor = JpInk,
            focusedTextColor = JpInk,
            unfocusedTextColor = JpInk
        ),
        shape = RoundedCornerShape(12.dp),
        minLines = minLines,
        modifier = modifier.fillMaxWidth()
    )
}

// Pre-crafted Templates Dialog
@Composable
fun TemplateSelectionDialog(
    onDismiss: () -> Unit,
    onSelectTemplate: (LetterData) -> Unit
) {
    val templates = listOf(
        TemplateItem(
            title = "💌 A Heartfelt Love Letter",
            desc = "Intimate note with handwriting style, wax heart, and sweet memories.",
            data = LetterData(
                recipient = "Sophie",
                salutation = "My dearest Sophie,",
                subject = "Some words deserve to be written down",
                bodyText = """I wanted to send you something you could hold in your hands.

In a world of fleeting notifications, take a breath and know how deeply appreciated you are. Every idea we spoke about has begun to flourish, and every sunrise feels a little warmer remembering our shared laughter.

May this little letter find you surrounded by quiet peace, a warm cup in hand, and the gentle reminder that great things take time to unfold.""",
                signOff = "With all my love,",
                sender = "Sabi",
                signatureText = "Sabi",
                headerStyle = HeaderStyle.ROMANTIC_PERSONAL,
                paperTheme = PaperTheme.CREAM_LAID,
                fontChoice = FontChoice.HANDWRITTEN,
                sealStyle = SealStyle.HEART,
                sealColor = SealColor.CRIMSON,
                showRuledLines = true
            )
        ),
        TemplateItem(
            title = "🏢 Official Executive Letterhead",
            desc = "Professional corporate memorandum with reference numbers and contact grid.",
            data = LetterData(
                recipient = "Board of Directors",
                recipientTitle = "Senior Executive Committee",
                salutation = "Dear Members of the Board,",
                subject = "Quarterly Strategic Milestone & Technology Roadmap",
                bodyText = """We are pleased to submit the strategic report for the upcoming fiscal cycle.

Over the past two quarters, our team has achieved seamless operational stability while deploying novel infrastructure enhancements across all primary domains. Client satisfaction indices have reached an unprecedented 98.4%, and efficiency metrics reflect significant improvements.

We look forward to presenting these findings during the annual summit and remain at your disposal for any further inquiries.""",
                signOff = "Respectfully submitted,",
                sender = "Dr. Alexander Wright",
                senderTitle = "Chief Strategy Officer",
                senderOrg = "VANGUARD INITIATIVE CORP.",
                senderAddress = "Suite 400, Financial Plaza, Frankfurt",
                senderEmail = "wright@vanguard-corp.eu",
                senderPhone = "+49 (0) 69 9876 5432",
                referenceNumber = "REF: VG-2026/Q3-EXEC",
                headerStyle = HeaderStyle.CORPORATE_OFFICIAL,
                paperTheme = PaperTheme.CRISP_WHITE,
                fontChoice = FontChoice.SERIF_CLASSIC,
                sealStyle = SealStyle.ROSE_CREST,
                sealColor = SealColor.ROYAL_NAVY,
                showRuledLines = false
            )
        ),
        TemplateItem(
            title = "💐 Deep Gratitude & Thank You",
            desc = "Warm appreciation letter for a mentor, colleague, or close friend.",
            data = LetterData(
                recipient = "Professor Elena Rostova",
                salutation = "Dear Professor Rostova,",
                subject = "Heartfelt Thanks for Your Guidance",
                bodyText = """I am writing to express my deepest gratitude for your wisdom, patience, and encouraging mentorship throughout this transformative year.

Your faith in my abilities gave me the courage to pursue difficult questions and seek honest answers. I will forever carry the lessons learned in your studio into my future work.

Thank you for being such an extraordinary inspiration.""",
                signOff = "With sincere gratitude and warmth,",
                sender = "Clara Vance",
                senderOrg = "Institute of Fine Arts",
                headerStyle = HeaderStyle.MINIMAL_MODERN,
                paperTheme = PaperTheme.VINTAGE_PARCHMENT,
                fontChoice = FontChoice.SERIF_CLASSIC,
                sealStyle = SealStyle.LILY,
                sealColor = SealColor.ANTIQUE_GOLD,
                showRuledLines = true
            )
        ),
        TemplateItem(
            title = "🎓 Letter of Recommendation",
            desc = "Official academic and professional endorsement letterhead.",
            data = LetterData(
                recipient = "Admissions Committee",
                salutation = "To Whom It May Concern:",
                subject = "Letter of Recommendation for Julian Meyer",
                bodyText = """It is my distinct privilege to recommend Julian Meyer for admission into your prestigious postgraduate fellowship program.

Having supervised Julian over the past three years, I have observed a rare combination of intellectual rigor, creative tenacity, and remarkable collegiality. He consistently approaches complex research problems with exceptional clarity and enthusiasm.

I offer Julian my highest and most unreserved recommendation.""",
                signOff = "Yours faithfully,",
                sender = "Prof. Marcus Sterling",
                senderTitle = "Chair of Applied Sciences",
                senderOrg = "University Research Council",
                headerStyle = HeaderStyle.CORPORATE_OFFICIAL,
                paperTheme = PaperTheme.CREAM_LAID,
                fontChoice = FontChoice.SERIF_CLASSIC,
                sealStyle = SealStyle.MONOGRAM,
                sealColor = SealColor.ROYAL_NAVY,
                initialMonogram = "M",
                showRuledLines = false
            )
        ),
        TemplateItem(
            title = "🌿 Artisan Studio Memo",
            desc = "Botanical aesthetic memo for craftspeople and creative agencies.",
            data = LetterData(
                recipient = "Our Cherished Patrons",
                salutation = "Dear Friends & Patrons,",
                subject = "Autumn Botanical Collection & Studio Notes",
                bodyText = """As the first autumn leaves begin to turn, our studio doors open to reveal this season's handcrafted creations.

Each piece in this collection was shaped by hand using traditional slow-craft methods, celebrating organic textures and timeless beauty. We are overjoyed to share this new chapter with you.

Come visit us whenever you find yourself in the neighborhood.""",
                signOff = "Warmly from the workshop,",
                sender = "Mira & Studio Artisans",
                senderOrg = "The Botanical Press",
                headerStyle = HeaderStyle.ARTISAN_BOTANICAL,
                paperTheme = PaperTheme.SAGE_MINT,
                fontChoice = FontChoice.HANDWRITTEN,
                sealStyle = SealStyle.BOW,
                sealColor = SealColor.SAGE_OLIVE,
                showRuledLines = true
            )
        )
    )

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = JpPinkBg),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, JpInk),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select a Letter Template",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = JpInk
                )
                Text(
                    text = "Choose from pre-crafted letterheads and notes. You can customize every detail afterwards!",
                    fontSize = 12.sp,
                    color = JpInkMedium
                )

                HorizontalDivider(color = Color(0x3333314E))

                templates.forEach { tpl ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JpPaperCream, RoundedCornerShape(14.dp))
                            .border(1.5.dp, JpInk, RoundedCornerShape(14.dp))
                            .clickable { onSelectTemplate(tpl.data) }
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = tpl.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = JpInk
                            )
                            Text(
                                text = tpl.desc,
                                fontSize = 12.sp,
                                color = JpInkMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, JpInk)
                ) {
                    Text(text = "Cancel", fontWeight = FontWeight.Bold, color = JpInk)
                }
            }
        }
    }
}

data class TemplateItem(
    val title: String,
    val desc: String,
    val data: LetterData
)
