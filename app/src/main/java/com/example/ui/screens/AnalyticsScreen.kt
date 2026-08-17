package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AnalyticsLogEntity
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet

@Composable
fun AnalyticsScreen(
    totalTokens: Int,
    avgLatency: Double,
    offlineCount: Int,
    onlineCount: Int,
    recentLogs: List<AnalyticsLogEntity>,
    modifier: Modifier = Modifier
) {
    val totalQueries = (offlineCount + onlineCount).coerceAtLeast(1)
    val offlineRatio = (offlineCount.toFloat() / totalQueries.toFloat()).coerceIn(0f, 1f)
    val energySavedTotal = offlineCount * 0.038

    val animatedOfflineRatio by animateFloatAsState(
        targetValue = offlineRatio,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "offline_ratio_anim"
    )

    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val textPrimaryColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val primaryAccent = if (isDark) ElectricCyan else Color(0xFF0F52BA)
    val emeraldAccent = if (isDark) EmeraldGreen else Color(0xFF047857)
    val violetAccent = if (isDark) NeonViolet else Color(0xFF6D28D9)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── Header ────────────────────────────────────────────────────────
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(emeraldAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = null,
                        tint = emeraldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Dasbor Analitik Personal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimaryColor
                    )
                    Text(
                        text = "Pelacakan efisiensi komputasi, privasi, dan penghematan daya",
                        fontSize = 11.sp,
                        color = textSecondaryColor
                    )
                }
            }
        }

        // ── 4 Metric Cards Grid ───────────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsMetricCard(
                    title = "Total Token",
                    value = "$totalTokens",
                    subtitle = "Diproses aman",
                    icon = Icons.Default.Storage,
                    tint = primaryAccent,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricCard(
                    title = "Latensi Rata-rata",
                    value = "%.0f ms".format(if (avgLatency > 0) avgLatency else 230.0),
                    subtitle = "Responsivitas",
                    icon = Icons.Default.Timer,
                    tint = emeraldAccent,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsMetricCard(
                    title = "Rasio Offline",
                    value = "${(offlineRatio * 100).toInt()}%",
                    subtitle = "$offlineCount/$totalQueries kueri",
                    icon = Icons.Default.Bolt,
                    tint = violetAccent,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricCard(
                    title = "Hemat Energi",
                    value = "%.2f mWh".format(energySavedTotal),
                    subtitle = "Eco compute",
                    icon = Icons.Default.Eco,
                    tint = emeraldAccent,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Bar Chart Offline vs Online ───────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF)
                ),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Distribusi Kueri",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimaryColor
                    )
                    Text(
                        text = "Offline (On-Device) vs Online (Cloud)",
                        fontSize = 10.sp,
                        color = textSecondaryColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Mini Bar Chart using Canvas
                    BarChartMini(
                        offlineCount = offlineCount,
                        onlineCount = onlineCount,
                        offlineColor = emeraldAccent,
                        onlineColor = primaryAccent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar rasio
                    LinearProgressIndicator(
                        progress = { animatedOfflineRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = emeraldAccent,
                        trackColor = primaryAccent.copy(alpha = 0.3f),
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(emeraldAccent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Offline $offlineCount (${(offlineRatio * 100).toInt()}%)",
                                fontSize = 10.sp,
                                color = emeraldAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(primaryAccent)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Online $onlineCount (${((1f - offlineRatio) * 100).toInt()}%)",
                                fontSize = 10.sp,
                                color = primaryAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // ── Riwayat Log Telemetri ─────────────────────────────────────────
        item {
            Text(
                text = "Riwayat Kueri & Latensi Terakhir",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimaryColor,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (recentLogs.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Belum ada riwayat analitik. Mulai mengobrol untuk mengumpulkan data.",
                        fontSize = 11.sp,
                        color = textSecondaryColor,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        } else {
            items(recentLogs.take(10)) { log ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isLogOffline = log.mode == "OFFLINE"
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isLogOffline) emeraldAccent else primaryAccent)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = log.modelName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimaryColor
                                )
                            }
                            Text(
                                text = "Token: ${log.tokenCount} • Penghematan daya aktif",
                                fontSize = 9.sp,
                                color = textSecondaryColor
                            )
                        }
                        Text(
                            text = "${log.latencyMs}ms",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLogOffline) emeraldAccent else primaryAccent
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun BarChartMini(
    offlineCount: Int,
    onlineCount: Int,
    offlineColor: Color,
    onlineColor: Color,
    modifier: Modifier = Modifier
) {
    val maxVal = maxOf(offlineCount, onlineCount, 1).toFloat()
    val animOffline by animateFloatAsState(
        targetValue = offlineCount / maxVal,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = 0.7f),
        label = "anim_offline_bar"
    )
    val animOnline by animateFloatAsState(
        targetValue = onlineCount / maxVal,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = 0.7f),
        label = "anim_online_bar"
    )

    Canvas(modifier = modifier) {
        val barWidth = size.width * 0.3f
        val spacing = size.width * 0.1f
        val totalBarArea = (barWidth * 2) + spacing
        val startX = (size.width - totalBarArea) / 2f
        val maxBarHeight = size.height * 0.85f
        val bottomY = size.height

        // Offline bar
        val offlineHeight = (animOffline * maxBarHeight).coerceAtLeast(8f)
        drawRoundRect(
            color = offlineColor,
            topLeft = Offset(startX, bottomY - offlineHeight),
            size = Size(barWidth, offlineHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
        )
        // Online bar
        val onlineHeight = (animOnline * maxBarHeight).coerceAtLeast(8f)
        drawRoundRect(
            color = onlineColor,
            topLeft = Offset(startX + barWidth + spacing, bottomY - onlineHeight),
            size = Size(barWidth, onlineHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
        )
    }
}

@Composable
fun AnalyticsMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    isDark: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF101725) else Color(0xFFFFFFFF)),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = tint
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
            )
        }
    }
}
