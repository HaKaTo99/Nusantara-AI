package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.WebView
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
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
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Code, 1 = Preview
    var isCopied by remember { mutableStateOf(false) }

    val cleanLang = language.trim().lowercase().removePrefix("language-").ifBlank { "code" }
    val displayLang = cleanLang.uppercase()
    val isPreviewable = cleanLang == "html" || cleanLang == "svg"

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val containerBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
    val headerBg = Color(0xFF1E293B)
    val codeBodyBg = Color(0xFF0F172A)
    val accentCyan = Color(0xFF00F2FE)
    val accentGreen = Color(0xFF00FFA3)

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
                        text = "Source Code",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPreviewable) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = codeBodyBg,
                            contentColor = accentCyan,
                            modifier = Modifier
                                .width(150.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("Code", fontSize = 10.sp, color = if (selectedTab == 0) accentCyan else Color.Gray) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("Preview", fontSize = 10.sp, color = if (selectedTab == 1) accentCyan else Color.Gray) }
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Copy Button with Feedback
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isCopied) accentGreen.copy(alpha = 0.2f) else Color(0xFF334155).copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, if (isCopied) accentGreen else Color(0xFF475569)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
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

            // Code Body
            if (selectedTab == 0 || !isPreviewable) {
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
                // Live HTML / SVG WebView Sandbox
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                loadDataWithBaseURL(null, code, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier.matchParentSize()
                    )
                }
            }
        }
    }
}
