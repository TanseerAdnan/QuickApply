package com.t.quickapply.data.extractor

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import org.apache.poi.xwpf.usermodel.XWPFDocument

object ResumeTextExtractor {

    fun extract(context: Context, uri: Uri): String {
        val mime = context.contentResolver.getType(uri)
        return when {
            mime == "application/pdf" -> extractPdf(context, uri)
            mime == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                extractDocx(context, uri)
            else -> ""
        }
    }

    private fun extractPdf(context: Context, uri: Uri): String {
        return try {
            PDFBoxResourceLoader.init(context)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                PDDocument.load(stream).use { doc ->
                    PDFTextStripper().getText(doc)
                }
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun extractDocx(context: Context, uri: Uri): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                XWPFDocument(stream).use { doc ->
                    doc.paragraphs.joinToString("\n") { it.text }
                }
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}