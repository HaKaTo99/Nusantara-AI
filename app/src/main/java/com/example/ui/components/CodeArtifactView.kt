package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.domain.ai.code.CodeExecutionEngine
import com.example.domain.ai.code.ExecutionResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CodeArtifactView(
    code: String,
    language: String = "code",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Kode, 1 = Live
    var isCopied by remember { mutableStateOf(false) }

    val cleanLang = language.trim().lowercase().removePrefix("language-").ifBlank { "code" }
    val displayLang = cleanLang.uppercase()
    val isWebVisual = cleanLang in listOf("html", "htm", "svg", "xml")

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val containerBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
    val headerBg = Color(0xFF1E293B)
    val codeBodyBg = Color(0xFF0F172A)
    val terminalBg = Color(0xFF090D16)
    val accentCyan = Color(0xFF00F2FE)
    val accentGreen = Color(0xFF00FFA3)
    val accentAmber = Color(0xFFF59E0B)

    // Execution state for non-visual languages
    var isExecuting by remember { mutableStateOf(false) }
    var executionResult by remember { mutableStateOf<ExecutionResult?>(null) }

    val runCode = {
        coroutineScope.launch {
            isExecuting = true
            delay(120) // Natural execution pulse
            val res = CodeExecutionEngine.execute(code, cleanLang, context)
            executionResult = res
            isExecuting = false
        }
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1 && !isWebVisual && executionResult == null) {
            runCode()
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = codeBodyBg,
        border = BorderStroke(1.dp, containerBorderColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(accentCyan.copy(alpha = 0.15f))
                            .border(1.dp, accentCyan.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = displayLang,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = accentCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isWebVisual) "Web Artifact" else "Live Executable",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Dual Tabs (Kode vs Live) & Copy Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = codeBodyBg,
                        contentColor = accentCyan,
                        modifier = Modifier
                            .width(160.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(10.dp), tint = if (selectedTab == 0) accentCyan else Color.Gray)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Kode", fontSize = 10.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal, color = if (selectedTab == 0) accentCyan else Color.Gray)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isWebVisual) Icons.Default.Visibility else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(11.dp),
                                        tint = if (selectedTab == 1) (if (isWebVisual) accentCyan else accentGreen) else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isWebVisual) "Live" else "Run Live",
                                        fontSize = 10.sp,
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == 1) (if (isWebVisual) accentCyan else accentGreen) else Color.Gray
                                    )
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Copy Button
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCopied) accentGreen.copy(alpha = 0.2f) else Color(0xFF334155).copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, if (isCopied) accentGreen else Color(0xFF475569)),
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("AI Code", code)
                                    clipboard.setPrimaryClip(clip)
                                    isCopied = true
                                    Toast.makeText(context, "Kode $displayLang berhasil disalin!", Toast.LENGTH_SHORT).show()
                                    coroutineScope.launch {
                                        delay(2000)
                                        isCopied = false
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Salin Kode",
                                tint = if (isCopied) accentGreen else Color(0xFFF1F5F9),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCopied) "Tersalin" else "Salin",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCopied) accentGreen else Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }

            // Body Switcher
            if (selectedTab == 0) {
                // Tab 0: Monospace Code View
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = code,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFF8FAFC),
                        lineHeight = 18.sp
                    )
                }
            } else {
                // Tab 1: Live Interactive Runner & Sandbox
                if (isWebVisual) {
                    // Visual HTML / SVG Live Sandbox
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 220.dp, max = 340.dp)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.loadWithOverviewMode = true
                                    settings.useWideViewPort = true
                                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                                    
                                    val renderedHtml = if (code.contains("<html", ignoreCase = true) || code.contains("<!DOCTYPE", ignoreCase = true)) {
                                        code
                                    } else if (cleanLang == "svg" || code.trim().startsWith("<svg", ignoreCase = true)) {
                                        """
                                        <!DOCTYPE html>
                                        <html>
                                        <head>
                                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                            <style>
                                                body { margin: 0; display: flex; align-items: center; justify-content: center; height: 100vh; background: #fafafa; }
                                                svg { max-width: 95%; max-height: 95%; }
                                            </style>
                                        </head>
                                        <body>$code</body>
                                        </html>
                                        """.trimIndent()
                                    } else {
                                        """
                                        <!DOCTYPE html>
                                        <html>
                                        <head>
                                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                            <style>
                                                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; padding: 12px; margin: 0; color: #1e293b; }
                                                button { background: #2563eb; color: white; border: none; padding: 8px 16px; border-radius: 6px; font-weight: bold; cursor: pointer; }
                                                button:active { opacity: 0.8; }
                                                input { padding: 6px 10px; border: 1px solid #cbd5e1; border-radius: 6px; }
                                            </style>
                                        </head>
                                        <body>$code</body>
                                        </html>
                                        """.trimIndent()
                                    }
                                    
                                    loadDataWithBaseURL(null, renderedHtml, "text/html", "UTF-8", null)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    // Executable Sandbox Console (Kotlin, Python, Java, SQL, Shell, etc.)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(terminalBg)
                            .padding(12.dp)
                    ) {
                        // Sub-header terminal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isExecuting) accentAmber else accentGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = executionResult?.executionType ?: "On-Device Execution Sandbox",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { runCode() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Jalankan Ulang",
                                    tint = accentCyan,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Jalankan Ulang", fontSize = 9.sp, color = accentCyan, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isExecuting) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = accentCyan, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Mengeksekusi di Sandbox...", fontSize = 11.sp, color = accentCyan, fontFamily = FontFamily.Monospace)
                            }
                        } else {
                            executionResult?.let { res ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF05080E))
                                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                                        .padding(10.dp)
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = res.output,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = if (res.isSuccess) accentGreen else Color(0xFFEF4444),
                                        lineHeight = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "⏱️ Latensi: ${res.executionTimeMs}ms",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = if (res.isSuccess) "✓ Status: EXIT_SUCCESS (0)" else "❌ Status: ERROR (${res.exitCode})",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (res.isSuccess) accentGreen else Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
