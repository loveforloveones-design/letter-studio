package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.FontChoice
import com.example.model.HeaderStyle
import com.example.model.LetterData
import com.example.model.SealStyle
import com.example.model.StickerType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

object PdfExporter {

    // Standard A4 dimensions in typographic points (72 points per inch)
    const val A4_WIDTH = 595
    const val A4_HEIGHT = 842

    fun generatePdf(context: Context, letter: LetterData): File? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        try {
            renderLetterOnCanvas(canvas, letter, A4_WIDTH.toFloat(), A4_HEIGHT.toFloat())
            document.finishPage(page)

            // Save to letters directory
            val outputDir = File(context.cacheDir, "letters").apply { mkdirs() }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val safeRecipient = letter.recipient.replace(Regex("[^a-zA-Z0-9]"), "_").take(15)
            val file = File(outputDir, "Letterhead_${safeRecipient}_$timeStamp.pdf")

            val outStream = FileOutputStream(file)
            document.writeTo(outStream)
            outStream.flush()
            outStream.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            document.close()
        }
    }

    fun renderLetterOnCanvas(canvas: Canvas, letter: LetterData, width: Float, height: Float) {
        val marginX = 48f
        var currentY = 52f

        // 1. Paper Background Fill
        val bgPaint = Paint().apply {
            color = AndroidColor.argb(
                (letter.paperTheme.paperColor.alpha * 255).toInt(),
                (letter.paperTheme.paperColor.red * 255).toInt(),
                (letter.paperTheme.paperColor.green * 255).toInt(),
                (letter.paperTheme.paperColor.blue * 255).toInt()
            )
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width, height, bgPaint)

        // 2. Ruled Lines (if enabled)
        if (letter.showRuledLines) {
            val linePaint = Paint().apply {
                color = AndroidColor.argb(
                    (letter.paperTheme.lineColor.alpha * 255).toInt(),
                    (letter.paperTheme.lineColor.red * 255).toInt(),
                    (letter.paperTheme.lineColor.green * 255).toInt(),
                    (letter.paperTheme.lineColor.blue * 255).toInt()
                )
                strokeWidth = 1f
                style = Paint.Style.STROKE
            }
            var lineY = 120f
            while (lineY < height - 60f) {
                canvas.drawLine(marginX, lineY, width - marginX, lineY, linePaint)
                lineY += 26f
            }
        }

        // 3. Document Outer Border
        val borderPaint = Paint().apply {
            color = AndroidColor.parseColor("#33314E")
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        val borderRect = RectF(24f, 24f, width - 24f, height - 24f)
        canvas.drawRoundRect(borderRect, 12f, 12f, borderPaint)

        // Text & Line Paints
        val inkPaint = Paint().apply {
            color = AndroidColor.parseColor("#33314E")
            isAntiAlias = true
        }

        val primaryTypeface = when (letter.fontChoice) {
            FontChoice.HANDWRITTEN -> Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            FontChoice.SERIF_CLASSIC -> Typeface.SERIF
            FontChoice.MONO_TYPEWRITER -> Typeface.MONOSPACE
            FontChoice.SANS_CLEAN -> Typeface.SANS_SERIF
        }

        // 4. Header according to style
        when (letter.headerStyle) {
            HeaderStyle.ROMANTIC_PERSONAL -> {
                inkPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                inkPaint.textSize = 14f
                canvas.drawText("A Note for You", marginX, currentY + 12f, inkPaint)

                inkPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                inkPaint.textSize = 12f
                canvas.drawText("from ${letter.sender}", marginX, currentY + 28f, inkPaint)

                inkPaint.typeface = Typeface.DEFAULT
                inkPaint.textSize = 11f
                val dateWidth = inkPaint.measureText(letter.date)
                canvas.drawText(letter.date, width - marginX - dateWidth, currentY + 16f, inkPaint)

                currentY += 42f
                canvas.drawLine(marginX, currentY, width - marginX, currentY, borderPaint)
                currentY += 24f
            }
            HeaderStyle.CORPORATE_OFFICIAL -> {
                inkPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                inkPaint.textSize = 16f
                canvas.drawText(letter.senderOrg.ifBlank { "VANGUARD INITIATIVE" }.uppercase(), marginX, currentY + 12f, inkPaint)

                if (letter.senderTitle.isNotBlank()) {
                    inkPaint.typeface = Typeface.DEFAULT
                    inkPaint.textSize = 11f
                    canvas.drawText(letter.senderTitle, marginX, currentY + 28f, inkPaint)
                }

                inkPaint.textSize = 10f
                val dateW = inkPaint.measureText(letter.date)
                canvas.drawText(letter.date, width - marginX - dateW, currentY + 12f, inkPaint)
                if (letter.referenceNumber.isNotBlank()) {
                    val refW = inkPaint.measureText(letter.referenceNumber)
                    canvas.drawText(letter.referenceNumber, width - marginX - refW, currentY + 28f, inkPaint)
                }

                currentY += 38f
                borderPaint.strokeWidth = 2.5f
                canvas.drawLine(marginX, currentY, width - marginX, currentY, borderPaint)
                borderPaint.strokeWidth = 1f
                canvas.drawLine(marginX, currentY + 3f, width - marginX, currentY + 3f, borderPaint)

                currentY += 16f
                inkPaint.textSize = 9.5f
                canvas.drawText(letter.senderAddress, marginX, currentY, inkPaint)
                val contactInfo = "${letter.senderEmail}  ·  ${letter.senderPhone}"
                val contactW = inkPaint.measureText(contactInfo)
                canvas.drawText(contactInfo, width - marginX - contactW, currentY, inkPaint)

                currentY += 10f
                canvas.drawLine(marginX, currentY, width - marginX, currentY, borderPaint)
                currentY += 24f
            }
            HeaderStyle.MINIMAL_MODERN -> {
                // Monogram mark box
                val monoRect = RectF(marginX, currentY, marginX + 28f, currentY + 28f)
                val fillPaint = Paint().apply {
                    color = AndroidColor.parseColor("#33314E")
                    style = Paint.Style.FILL
                }
                canvas.drawRoundRect(monoRect, 6f, 6f, fillPaint)

                val whiteTextPaint = Paint().apply {
                    color = AndroidColor.WHITE
                    textSize = 15f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }
                val char = letter.initialMonogram.ifBlank { "S" }.uppercase()
                canvas.drawText(char, marginX + 8f, currentY + 20f, whiteTextPaint)

                inkPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                inkPaint.textSize = 13f
                canvas.drawText(letter.sender.uppercase(), marginX + 38f, currentY + 14f, inkPaint)

                inkPaint.typeface = Typeface.DEFAULT
                inkPaint.textSize = 10f
                canvas.drawText(letter.senderOrg.ifBlank { "Independent Studio" }, marginX + 38f, currentY + 26f, inkPaint)

                val dWidth = inkPaint.measureText(letter.date)
                canvas.drawText(letter.date, width - marginX - dWidth, currentY + 20f, inkPaint)

                currentY += 38f
                canvas.drawLine(marginX, currentY, width - marginX, currentY, borderPaint)
                currentY += 24f
            }
            HeaderStyle.VINTAGE_CREST -> {
                inkPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                inkPaint.textSize = 13f
                val title = "✦  ${letter.senderOrg.ifBlank { "EX LIBRIS ET EPISTOLA" }.uppercase()}  ✦"
                val titleW = inkPaint.measureText(title)
                canvas.drawText(title, (width - titleW) / 2f, currentY + 12f, inkPaint)

                inkPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                inkPaint.textSize = 10.5f
                val desk = "— From the Desk of ${letter.sender} —"
                val deskW = inkPaint.measureText(desk)
                canvas.drawText(desk, (width - deskW) / 2f, currentY + 28f, inkPaint)

                currentY += 40f
                canvas.drawLine(marginX, currentY, width - marginX, currentY, borderPaint)
                currentY += 20f
            }
            HeaderStyle.ARTISAN_BOTANICAL -> {
                inkPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                inkPaint.textSize = 15f
                canvas.drawText("🌿  ${letter.senderOrg.ifBlank { "The Artisan Press" }}", marginX, currentY + 14f, inkPaint)

                inkPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                inkPaint.textSize = 11f
                canvas.drawText("Crafted with care · ${letter.sender}", marginX + 28f, currentY + 30f, inkPaint)

                inkPaint.typeface = Typeface.DEFAULT
                inkPaint.textSize = 11f
                val dW = inkPaint.measureText(letter.date)
                canvas.drawText(letter.date, width - marginX - dW, currentY + 20f, inkPaint)

                currentY += 42f
                canvas.drawLine(marginX, currentY, width - marginX, currentY, borderPaint)
                currentY += 24f
            }
        }

        // 5. Subject Line
        if (letter.subject.isNotBlank()) {
            inkPaint.typeface = Typeface.create(primaryTypeface, Typeface.BOLD)
            inkPaint.textSize = 13.5f
            canvas.drawText("Subject: ${letter.subject}", marginX, currentY, inkPaint)
            currentY += 22f
        }

        // 6. Salutation
        inkPaint.typeface = Typeface.create(primaryTypeface, Typeface.BOLD)
        inkPaint.textSize = 14f
        val salutationText = letter.salutation.ifBlank { "Dear ${letter.recipient}," }
        canvas.drawText(salutationText, marginX, currentY, inkPaint)
        currentY += 24f

        // 7. Body Text Wrapping
        inkPaint.typeface = primaryTypeface
        inkPaint.textSize = 12f
        val maxTextWidth = width - (marginX * 2f)
        val lineHeight = 20f

        val paragraphs = letter.bodyText.split("\n")
        for (paragraph in paragraphs) {
            if (paragraph.isBlank()) {
                currentY += lineHeight * 0.75f
                continue
            }
            val words = paragraph.split(" ")
            var lineBuilder = StringBuilder()

            for (word in words) {
                val testLine = if (lineBuilder.isEmpty()) word else "$lineBuilder $word"
                if (inkPaint.measureText(testLine) <= maxTextWidth) {
                    lineBuilder.append(if (lineBuilder.isEmpty()) word else " $word")
                } else {
                    canvas.drawText(lineBuilder.toString(), marginX, currentY, inkPaint)
                    currentY += lineHeight
                    lineBuilder = StringBuilder(word)
                }
            }
            if (lineBuilder.isNotEmpty()) {
                canvas.drawText(lineBuilder.toString(), marginX, currentY, inkPaint)
                currentY += lineHeight
            }
            currentY += 6f
        }

        currentY += 20f

        // 8. Sign-off and Signature
        val rightAlignX = width - marginX - 140f
        inkPaint.typeface = Typeface.create(primaryTypeface, Typeface.ITALIC)
        inkPaint.textSize = 12.5f
        canvas.drawText(letter.signOff, rightAlignX, currentY, inkPaint)
        currentY += 26f

        // Signature text
        inkPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC)
        inkPaint.textSize = 20f
        canvas.drawText(letter.signatureText.ifBlank { letter.sender }, rightAlignX, currentY, inkPaint)

        // 9. Wax Seal Stamp Vector
        val sealCenterX = width - marginX - 40f
        val sealCenterY = currentY - 8f
        drawWaxSealOnCanvas(canvas, letter.sealStyle, letter.sealColor.hexString, sealCenterX, sealCenterY, 32f, letter.initialMonogram)

        // 10. Stickers rendered on PDF
        for (sticker in letter.stickers) {
            val sx = sticker.posX * width
            val sy = sticker.posY * height
            drawStickerOnCanvas(canvas, sticker.type, sx, sy, 36f * sticker.scale)
        }
    }

    private fun drawWaxSealOnCanvas(
        canvas: Canvas,
        sealStyle: SealStyle,
        hexColor: String,
        cx: Float,
        cy: Float,
        r: Float,
        monogram: String
    ) {
        val baseColor = AndroidColor.parseColor(hexColor)
        val sealPaint = Paint().apply {
            color = baseColor
            this.style = Paint.Style.FILL
            isAntiAlias = true
        }
        val strokePaint = Paint().apply {
            color = AndroidColor.parseColor("#33314E")
            strokeWidth = 2f
            this.style = Paint.Style.STROKE
            isAntiAlias = true
        }

        // Draw wax puddle
        val path = AndroidPath()
        val points = 16
        for (i in 0 until points) {
            val angle = (i * (2 * Math.PI / points)).toFloat()
            val wobble = when (i % 4) {
                0 -> 1.05f
                1 -> 0.94f
                2 -> 1.02f
                else -> 0.97f
            }
            val px = cx + (r * wobble) * cos(angle)
            val py = cy + (r * wobble) * sin(angle)
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()

        canvas.drawPath(path, sealPaint)
        canvas.drawPath(path, strokePaint)

        // Inner rim
        val innerR = r * 0.72f
        strokePaint.strokeWidth = 1.2f
        canvas.drawCircle(cx, cy, innerR, strokePaint)

        // Emblem
        when (sealStyle) {
            SealStyle.HEART -> {
                val heart = AndroidPath().apply {
                    val s = innerR * 0.55f
                    moveTo(cx, cy + s * 0.85f)
                    cubicTo(cx - s * 1.1f, cy + s * 0.25f, cx - s * 1.1f, cy - s * 0.75f, cx - s * 0.45f, cy - s * 0.75f)
                    cubicTo(cx - s * 0.1f, cy - s * 0.75f, cx, cy - s * 0.45f, cx, cy - s * 0.45f)
                    cubicTo(cx, cy - s * 0.45f, cx + s * 0.1f, cy - s * 0.75f, cx + s * 0.45f, cy - s * 0.75f)
                    cubicTo(cx + s * 1.1f, cy - s * 0.75f, cx + s * 1.1f, cy + s * 0.25f, cx, cy + s * 0.85f)
                    close()
                }
                strokePaint.strokeWidth = 1.8f
                canvas.drawPath(heart, strokePaint)
            }
            SealStyle.BOW -> {
                val s = innerR * 0.6f
                strokePaint.strokeWidth = 1.6f
                canvas.drawLine(cx - s * 0.5f, cy - s * 0.3f, cx + s * 0.5f, cy + s * 0.3f, strokePaint)
                canvas.drawLine(cx - s * 0.5f, cy + s * 0.3f, cx + s * 0.5f, cy - s * 0.3f, strokePaint)
                canvas.drawCircle(cx, cy, s * 0.2f, strokePaint)
            }
            SealStyle.TEDDY -> {
                val s = innerR * 0.5f
                strokePaint.strokeWidth = 1.4f
                canvas.drawCircle(cx - s * 0.55f, cy - s * 0.5f, s * 0.3f, strokePaint)
                canvas.drawCircle(cx + s * 0.55f, cy - s * 0.5f, s * 0.3f, strokePaint)
                canvas.drawCircle(cx, cy + s * 0.1f, s * 0.7f, strokePaint)
            }
            SealStyle.STAR -> {
                val star = AndroidPath()
                val pts = 5
                val s = innerR * 0.6f
                for (i in 0 until pts * 2) {
                    val radius = if (i % 2 == 0) s else s * 0.48f
                    val angle = (i * Math.PI / pts - Math.PI / 2).toFloat()
                    val px = cx + radius * cos(angle)
                    val py = cy + radius * sin(angle)
                    if (i == 0) star.moveTo(px, py) else star.lineTo(px, py)
                }
                star.close()
                strokePaint.strokeWidth = 1.6f
                canvas.drawPath(star, strokePaint)
            }
            else -> {
                val textPaint = Paint().apply {
                    color = AndroidColor.parseColor("#33314E")
                    textSize = innerR * 0.9f
                    typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                    isAntiAlias = true
                }
                val letterChar = monogram.ifBlank { "S" }.uppercase().take(1)
                val w = textPaint.measureText(letterChar)
                canvas.drawText(letterChar, cx - (w / 2f), cy + (innerR * 0.35f), textPaint)
            }
        }
    }

    private fun drawStickerOnCanvas(canvas: Canvas, type: StickerType, x: Float, y: Float, size: Float) {
        val strokePaint = Paint().apply {
            color = AndroidColor.parseColor("#33314E")
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        when (type) {
            StickerType.POSTAGE_STAMP -> {
                val stampRect = RectF(x - size * 0.6f, y - size * 0.7f, x + size * 0.6f, y + size * 0.7f)
                val stampPaint = Paint().apply {
                    color = AndroidColor.parseColor("#FDF6DF")
                    style = Paint.Style.FILL
                }
                canvas.drawRoundRect(stampRect, 4f, 4f, stampPaint)
                canvas.drawRoundRect(stampRect, 4f, 4f, strokePaint)
                // stamp inner pink square
                val innerRect = RectF(stampRect.left + 4f, stampRect.top + 4f, stampRect.right - 4f, stampRect.bottom - 4f)
                val pinkPaint = Paint().apply { color = AndroidColor.parseColor("#F8E2EB") }
                canvas.drawRect(innerRect, pinkPaint)
                canvas.drawRect(innerRect, strokePaint)
            }
            StickerType.LILY, StickerType.BOW -> {
                val circlePaint = Paint().apply {
                    color = AndroidColor.parseColor("#FF9EC0")
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(x, y, size * 0.35f, circlePaint)
                canvas.drawCircle(x, y, size * 0.35f, strokePaint)
            }
            StickerType.WAX_HEART -> {
                val heartPaint = Paint().apply {
                    color = AndroidColor.parseColor("#C1392B")
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(x, y, size * 0.32f, heartPaint)
                canvas.drawCircle(x, y, size * 0.32f, strokePaint)
            }
            StickerType.STAR -> {
                val starPaint = Paint().apply {
                    color = AndroidColor.parseColor("#FFD75E")
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(x, y, size * 0.35f, starPaint)
                canvas.drawCircle(x, y, size * 0.35f, strokePaint)
            }
            else -> {
                val defaultPaint = Paint().apply {
                    color = AndroidColor.parseColor("#FFFDF8")
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(x, y, size * 0.3f, defaultPaint)
                canvas.drawCircle(x, y, size * 0.3f, strokePaint)
            }
        }
    }

    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Personalized Letterhead Document")
            putExtra(Intent.EXTRA_TEXT, "Here is your customized letterhead document.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share Letterhead PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun viewPdf(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No PDF viewer found. File saved: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }

    fun printLetter(context: Context, letter: LetterData) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(context, "Print service is not available", Toast.LENGTH_SHORT).show()
            return
        }

        val printAdapter = object : PrintDocumentAdapter() {
            private var pdfFile: File? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                pdfFile = generatePdf(context, letter)
                if (pdfFile == null) {
                    callback?.onLayoutFailed("Failed to render PDF for printing")
                    return
                }

                val info = PrintDocumentInfo.Builder("letterhead_${letter.recipient}.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                val input = pdfFile?.let { FileInputStream(it) }
                val output = destination?.let { FileOutputStream(it.fileDescriptor) }

                try {
                    if (input != null && output != null) {
                        val buf = ByteArray(1024)
                        var bytesRead: Int
                        while (input.read(buf).also { bytesRead = it } > 0) {
                            output.write(buf, 0, bytesRead)
                        }
                        callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                    } else {
                        callback?.onWriteFailed("Cannot open print stream")
                    }
                } catch (e: IOException) {
                    callback?.onWriteFailed(e.message)
                } finally {
                    try { input?.close() } catch (ignored: Exception) {}
                    try { output?.close() } catch (ignored: Exception) {}
                }
            }
        }

        printManager.print("Letterhead_${letter.recipient}", printAdapter, PrintAttributes.Builder().build())
    }
}
