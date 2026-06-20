package com.t.quickapply.data.extractor

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.File

object ResumeWriter {

    private val SECTION_HEADERS = setOf(
        "experience", "education", "skills", "summary", "objective",
        "projects", "certifications", "languages", "awards",
        "publications", "interests", "references", "profile",
        "work experience", "professional experience", "technical skills"
    )

    fun writeEnhancedPdf(
        context: Context,
        cvId: String,
        text: String
    ): String {
        PDFBoxResourceLoader.init(context)

        val document = PDDocument()
        val pageWidth = PDRectangle.A4.width
        val pageHeight = PDRectangle.A4.height
        val marginX = 55f
        val marginTop = 60f
        val marginBottom = 50f
        val usableWidth = pageWidth - 2 * marginX

        // Parse the text into structured lines
        val parsedLines = parseLines(text)

        // Layout state
        var currentY = pageHeight - marginTop
        var page = PDPage(PDRectangle.A4)
        document.addPage(page)
        var stream = PDPageContentStream(document, page)

        fun newPage() {
            stream.close()
            page = PDPage(PDRectangle.A4)
            document.addPage(page)
            stream = PDPageContentStream(document, page)
            currentY = pageHeight - marginTop
        }

        fun needsNewPage(requiredHeight: Float): Boolean {
            return currentY - requiredHeight < marginBottom
        }

        fun drawLine(y: Float) {
            stream.setStrokingColor(0.75f, 0.75f, 0.75f)
            stream.setLineWidth(0.5f)
            stream.moveTo(marginX, y)
            stream.lineTo(pageWidth - marginX, y)
            stream.stroke()
        }

        fun drawText(
            text: String,
            x: Float,
            y: Float,
            font: PDType1Font,
            size: Float,
            r: Float = 0f,
            g: Float = 0f,
            b: Float = 0f
        ) {
            stream.beginText()
            stream.setFont(font, size)
            stream.setNonStrokingColor(r, g, b)
            stream.newLineAtOffset(x, y)
            stream.showText(sanitize(text))
            stream.endText()
        }

        // Wrap a string to fit within maxWidth, return list of lines
        fun wrapText(text: String, font: PDType1Font, size: Float, maxWidth: Float): List<String> {
            val words = text.split(" ")
            val lines = mutableListOf<String>()
            var current = ""
            for (word in words) {
                val test = if (current.isEmpty()) word else "$current $word"
                val w = try { font.getStringWidth(sanitize(test)) / 1000 * size } catch (e: Exception) { 0f }
                if (w > maxWidth && current.isNotEmpty()) {
                    lines.add(current)
                    current = word
                } else {
                    current = test
                }
            }
            if (current.isNotEmpty()) lines.add(current)
            return lines.ifEmpty { listOf("") }
        }

        // Draw each parsed line
        for (item in parsedLines) {
            when (item.type) {

                LineType.NAME -> {
                    if (needsNewPage(40f)) newPage()
                    drawText(item.content, marginX, currentY, PDType1Font.HELVETICA_BOLD, 20f, 0.1f, 0.1f, 0.1f)
                    currentY -= 28f
                }

                LineType.CONTACT -> {
                    if (needsNewPage(16f)) newPage()
                    drawText(item.content, marginX, currentY, PDType1Font.HELVETICA, 9f, 0.4f, 0.4f, 0.4f)
                    currentY -= 14f
                }

                LineType.SECTION_HEADER -> {
                    if (needsNewPage(30f)) newPage()
                    currentY -= 10f
                    drawText(item.content.uppercase(), marginX, currentY, PDType1Font.HELVETICA_BOLD, 10f, 0.2f, 0.4f, 0.8f)
                    currentY -= 4f
                    drawLine(currentY)
                    currentY -= 10f
                }

                LineType.BULLET -> {
                    val bulletIndent = marginX + 10f
                    val textIndent = bulletIndent + 8f
                    val wrappedWidth = usableWidth - 18f
                    val wrapped = wrapText(item.content, PDType1Font.HELVETICA, 10f, wrappedWidth)
                    val totalH = wrapped.size * 13f
                    if (needsNewPage(totalH)) newPage()
                    // Draw bullet dot
                    stream.addRect(bulletIndent, currentY + 3f, 2.5f, 2.5f)
                    stream.fill()
                    wrapped.forEachIndexed { i, line ->
                        drawText(line, if (i == 0) textIndent else textIndent, currentY - i * 13f,
                            PDType1Font.HELVETICA, 10f)
                    }
                    currentY -= totalH + 2f
                }

                LineType.BOLD_LINE -> {
                    val wrapped = wrapText(item.content, PDType1Font.HELVETICA_BOLD, 10.5f, usableWidth)
                    val totalH = wrapped.size * 14f
                    if (needsNewPage(totalH)) newPage()
                    wrapped.forEachIndexed { i, line ->
                        drawText(line, marginX, currentY - i * 14f, PDType1Font.HELVETICA_BOLD, 10.5f)
                    }
                    currentY -= totalH + 2f
                }

                LineType.NORMAL -> {
                    if (item.content.isBlank()) {
                        currentY -= 6f
                        continue
                    }
                    val wrapped = wrapText(item.content, PDType1Font.HELVETICA, 10f, usableWidth)
                    val totalH = wrapped.size * 13f
                    if (needsNewPage(totalH)) newPage()
                    wrapped.forEachIndexed { i, line ->
                        drawText(line, marginX, currentY - i * 13f, PDType1Font.HELVETICA, 10f)
                    }
                    currentY -= totalH + 2f
                }
            }
        }

        stream.close()

        val dir = File(context.filesDir, "enhanced_cvs").also { it.mkdirs() }
        val file = File(dir, "enhanced_${cvId}.pdf")
        document.save(file)
        document.close()

        return file.absolutePath
    }

    // ── Parsing ──────────────────────────────────────────────────────────────

    enum class LineType { NAME, CONTACT, SECTION_HEADER, BULLET, BOLD_LINE, NORMAL }

    data class ParsedLine(val type: LineType, val content: String)

    private fun parseLines(text: String): List<ParsedLine> {
        val rawLines = text.lines()
        val result = mutableListOf<ParsedLine>()
        var firstNonEmpty = true

        for (raw in rawLines) {
            val trimmed = raw.trim()

            if (trimmed.isBlank()) {
                result.add(ParsedLine(LineType.NORMAL, ""))
                continue
            }

            // First non-empty line = candidate for name
            if (firstNonEmpty) {
                firstNonEmpty = false
                // Name: short, no numbers, not a section header
                if (trimmed.length < 50 && !trimmed.any { it.isDigit() }
                    && trimmed.lowercase() !in SECTION_HEADERS) {
                    result.add(ParsedLine(LineType.NAME, trimmed))
                    continue
                }
            }

            // Contact line: contains @ or phone-like patterns
            if (trimmed.contains("@") ||
                trimmed.contains(Regex("\\+?\\d[\\d\\s\\-]{6,}")) ||
                trimmed.contains("linkedin", ignoreCase = true) ||
                trimmed.contains("github", ignoreCase = true)) {
                result.add(ParsedLine(LineType.CONTACT, trimmed))
                continue
            }

            // Section header: short, matches known headers or ALL CAPS
            val lower = trimmed.lowercase().trimEnd(':')
            if (lower in SECTION_HEADERS ||
                (trimmed == trimmed.uppercase() && trimmed.length < 40 && trimmed.any { it.isLetter() })) {
                result.add(ParsedLine(LineType.SECTION_HEADER, trimmed.trimEnd(':')))
                continue
            }

            // Bullet: starts with -, •, *, or similar
            if (trimmed.startsWith("-") || trimmed.startsWith("•") ||
                trimmed.startsWith("*") || trimmed.startsWith("·")) {
                result.add(ParsedLine(LineType.BULLET, trimmed.trimStart('-', '•', '*', '·', ' ')))
                continue
            }

            // Bold candidate: short line that looks like a job title / company / date range
            val looksLikeBoldLine = trimmed.length < 80 &&
                    (trimmed.contains("|") ||
                            trimmed.contains("–") ||
                            trimmed.contains("-") && trimmed.count { it == '-' } == 1 ||
                            trimmed.any { it.isUpperCase() } && trimmed.any { it.isLowerCase() } &&
                            trimmed.split(" ").size <= 8)
            if (looksLikeBoldLine) {
                result.add(ParsedLine(LineType.BOLD_LINE, trimmed))
                continue
            }

            result.add(ParsedLine(LineType.NORMAL, trimmed))
        }

        return result
    }

    private fun sanitize(input: String): String =
        input.map { c -> if (c.code in 32..126) c else '?' }.joinToString("")
}