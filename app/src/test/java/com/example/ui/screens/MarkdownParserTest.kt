package com.example.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MarkdownParserTest {

    @Test
    fun testParseBoldAndHeaders() {
        val raw = "### 🏛️ Nusantara AI\n**Nusantara AI** dirancang oleh **Herman Krisnanto**."
        val annotated = parseMarkdownToAnnotatedString(
            rawText = raw,
            baseColor = Color.Black,
            isDark = false
        )

        val plainText = annotated.text
        // Verify raw symbols ### and ** are stripped out from plain string
        assertFalse(plainText.contains("###"))
        assertFalse(plainText.contains("**"))
        assertTrue(plainText.contains("🏛️ Nusantara AI"))
        assertTrue(plainText.contains("Nusantara AI dirancang oleh Herman Krisnanto."))

        // Verify span styles applied
        val spanStyles = annotated.spanStyles
        assertTrue(spanStyles.isNotEmpty())
        
        // Has bold styles for Herman Krisnanto and Nusantara AI
        val hasBold = spanStyles.any { it.item.fontWeight == FontWeight.Bold }
        assertTrue(hasBold)
    }

    @Test
    fun testParseItalicsAndCode() {
        val raw = "Menggunakan *On-Device Neural Quantization* dan `AES-256-GCM`."
        val annotated = parseMarkdownToAnnotatedString(
            rawText = raw,
            baseColor = Color.Black,
            isDark = false
        )

        val plainText = annotated.text
        assertFalse(plainText.contains("*On-Device"))
        assertFalse(plainText.contains("`AES-256-GCM`"))
        assertTrue(plainText.contains("On-Device Neural Quantization"))
        assertTrue(plainText.contains("AES-256-GCM"))

        val hasItalic = annotated.spanStyles.any { it.item.fontStyle == FontStyle.Italic }
        assertTrue(hasItalic)
    }

    @Test
    fun testParseMessageSegments() {
        val raw = """
            Berikut adalah implementasi kode:
            ```kotlin
            fun main() {
                println("Hello Nusantara")
            }
            ```
            Penjelasan:
            - Kode ini berjalan cepat
        """.trimIndent()

        val segments = parseMessageSegments(raw)
        assertEquals(3, segments.size)

        assertTrue(segments[0] is MessageSegment.Text)
        assertEquals("Berikut adalah implementasi kode:", (segments[0] as MessageSegment.Text).content)

        assertTrue(segments[1] is MessageSegment.Code)
        val codeSegment = segments[1] as MessageSegment.Code
        assertEquals("kotlin", codeSegment.language)
        assertTrue(codeSegment.code.contains("Hello Nusantara"))

        assertTrue(segments[2] is MessageSegment.Text)
        assertTrue((segments[2] as MessageSegment.Text).content.contains("Penjelasan:"))
    }
}
