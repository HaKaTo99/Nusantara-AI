package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.sync.SyncState

@Composable
fun TopAppBarWithStatus(
    isOnline: Boolean,
    modePreference: String, // "ONLINE", "OFFLINE", "HYBRID"
    selectedModel: String,
    syncState: SyncState,
    onModelClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onSyncClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDiagnosticsClick: (() -> Unit)? = null,
    onMeshClick: (() -> Unit)? = null,
    onEnterpriseClick: (() -> Unit)? = null,
    onSovereignAGIClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val emeraldTint = if (isDark) Color(0xFF00FFA3) else Color(0xFF047857)
    val amberTint = if (isDark) Color(0xFFFFB300) else Color(0xFFB45309)
    val primaryTint = MaterialTheme.colorScheme.primary

    val shortModelName = when {
        selectedModel.contains("Gemini 3.5 Flash", ignoreCase = true) -> "Gemini 3.5 Flash"
        selectedModel.contains("Gemini 3.1 Pro", ignoreCase = true) -> "Gemini 3.1 Pro"
        selectedModel.contains("Deepseek", ignoreCase = true) -> "DeepSeek R1"
        selectedModel.contains("Qwen", ignoreCase = true) -> "Qwen 2.5"
        selectedModel.contains("Gemma", ignoreCase = true) -> "Gemma 2"
        selectedModel.contains("Llama", ignoreCase = true) -> "Llama 3.3"
        selectedModel.contains("Mistral", ignoreCase = true) -> "Mistral Nemo"
        else -> selectedModel.take(18)
    }

    Surface(
        color = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF),
        tonalElevation = if (isDark) 2.dp else 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .border(
                width = if (isDark) 0.dp else 1.dp,
                color = if (isDark) Color.Transparent else Color(0xFFE2E8F0)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Clean Model Selector Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDark) Color(0xFF182235) else Color(0xFFF1F5F9),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onModelClick() }
                    .testTag("model_selector_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) emeraldTint else amberTint)
                    )

                    Spacer(modifier = Modifier.width(7.dp))

                    Text(
                        text = "Nusantara AI",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                    )

                    Text(
                        text = " • ",
                        fontSize = 13.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )

                    Text(
                        text = shortModelName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryTint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Pilih Model",
                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Right: Essential Controls (Security Vault & Settings)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Security Vault Button
                IconButton(
                    onClick = onSecurityClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("security_badge_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security Vault",
                        tint = emeraldTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Settings Button
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan",
                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
