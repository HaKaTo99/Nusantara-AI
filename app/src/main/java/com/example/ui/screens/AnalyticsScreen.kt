package com.example.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.drawscope.DrawScope
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
                        .background(EmeraldGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Dasbor Analitik Personal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Pelacakan efisiensi komputasi, privasi, dan penghematan daya",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    tint = ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricCard(
                    title = "Latensi Rata-rata",
                    value = "%.0f ms".format(if (avgLatency > 0) avgLatency else 230.0),
                    subtitle = "Responsivitas",
                    icon = Icons.Default.Timer,
                    tint = EmeraldGreen,
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
                    tint = NeonViolet,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsMetricCard(
                    title = "Hemat Energi",
                    value = "%.2f mWh".format(energySavedTotal),
                    subtitle = "Eco compute",
                    icon = Icons.Default.Eco,
                    tint = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Bar Chart Offline vs Online ───────────────────────────────────
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Distribusi Kueri",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Offline (On-Device) vs Online (Cloud)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Mini Bar Chart using Canvas
                    BarChartMini(
                        offlineCount = offlineCount,
                        onlineCount = onlineCount,
                        offlineColor = EmeraldGreen,
                        onlineColor = ElectricCyan,
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
                        color = EmeraldGreen,
                        trackColor = ElectricCyan.copy(alpha = 0.3f),
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
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$offlineCount On-Device",
                                fontSize = 11.sp,
                                color = EmeraldGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$onlineCount Cloud",
                                fontSize = 11.sp,
                                color = ElectricCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // ── Kategori Breakdown ────────────────────────────────────────────
        item {
            val categories = recentLogs
                .groupBy { it.category }
                .mapValues { it.value.size }
                .entries.sortedByDescending { it.value }

            if (categories.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Kategori Penggunaan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val maxCount = categories.firstOrNull()?.value ?: 1
                        categories.take(5).forEach { (cat, count) ->
                            val ratio by animateFloatAsState(
                                targetValue = count.toFloat() / maxCount,
                                animationSpec = spring(stiffness = Spring.StiffnessLow),
                                label = "cat_$cat"
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    modifier = Modifier.width(72.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(50)),
                                    color = NeonViolet,
                                    trackColor = NeonViolet.copy(alpha = 0.1f),
                                    strokeCap = StrokeCap.Round
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$count",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonViolet,
                                    modifier = Modifier.width(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Riwayat Audit Real-Time ───────────────────────────────────────
        item {
            Text(
                text = "📋 Riwayat Audit Kueri Real-Time",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(recentLogs) { log ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (log.mode == "OFFLINE") EmeraldGreen else ElectricCyan
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (log.mode == "OFFLINE") "OFFLINE" else "ONLINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (log.mode == "OFFLINE") EmeraldGreen else ElectricCyan
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = log.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = log.modelName,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${log.tokenCount} tok",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${log.latencyMs}ms",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

/**
 * Mini bar chart sederhana berbasis Canvas untuk visualisasi Offline vs Online.
 */
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
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
