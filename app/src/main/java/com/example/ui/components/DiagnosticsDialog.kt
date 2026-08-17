package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.ai.telemetry.NPUTelemetrySnapshot
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet

@Composable
fun DiagnosticsDialog(
    telemetry: NPUTelemetrySnapshot,
    onDismiss: () -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val textPrimaryColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val textSecondaryColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF334155)
    val primaryAccent = if (isDark) ElectricCyan else Color(0xFF0F52BA)
    val emeraldAccent = if (isDark) EmeraldGreen else Color(0xFF047857)
    val cardBg = if (isDark) Color(0xFF0A0F1D) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF223147) else Color(0xFFCBD5E1)
    val chipBg = if (isDark) Color(0xFF131D31) else Color(0xFFF1F5F9)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, cardBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(primaryAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Diagnostik NPU",
                                tint = primaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Diagnostik NPU & Telemetri",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = textPrimaryColor
                            )
                            Text(
                                text = "Native llama.cpp • Fase 3 Live",
                                style = MaterialTheme.typography.bodySmall,
                                color = primaryAccent
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = textSecondaryColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Accelerator Chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = chipBg,
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = emeraldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Akselerator Hardware Aktif",
                                fontSize = 11.sp,
                                color = textSecondaryColor
                            )
                            Text(
                                text = telemetry.activeAccelerator,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimaryColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Metric Grid (2x2)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Throughput Card
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Throughput AI",
                        value = "%.1f".format(telemetry.tokensPerSecond),
                        unit = "token/detik",
                        accentColor = primaryAccent,
                        chipBg = chipBg,
                        cardBorder = cardBorder,
                        textSecondaryColor = textSecondaryColor
                    )

                    // TTFT Card
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Latency (TTFT)",
                        value = "${telemetry.timeToFirstTokenMs}",
                        unit = "milidetik (ms)",
                        accentColor = Color(0xFFD97706),
                        chipBg = chipBg,
                        cardBorder = cardBorder,
                        textSecondaryColor = textSecondaryColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // RAM Footprint Card
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Alokasi RAM (RSS)",
                        value = "${telemetry.ramRssFootprintMB}",
                        unit = "MB (use_mmap)",
                        accentColor = if (isDark) NeonViolet else Color(0xFF7E22CE),
                        chipBg = chipBg,
                        cardBorder = cardBorder,
                        textSecondaryColor = textSecondaryColor
                    )

                    // Eco Compute Card
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Eco-Compute",
                        value = "%.2f".format(telemetry.totalOfflineEnergySavedMWh),
                        unit = "mWh Hemat",
                        accentColor = emeraldAccent,
                        chipBg = chipBg,
                        cardBorder = cardBorder,
                        textSecondaryColor = textSecondaryColor
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Thermal Status Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = chipBg,
                    border = BorderStroke(1.dp, cardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Thermostat,
                                    contentDescription = null,
                                    tint = if (telemetry.isThrottlingActive) Color.Red else emeraldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Suhu Baterai: %.1f°C".format(telemetry.batteryTemperatureC),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textPrimaryColor
                                )
                            }

                            Text(
                                text = telemetry.thermalStatus,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (telemetry.isThrottlingActive) Color.Red else emeraldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        val thermalProgress = (telemetry.batteryTemperatureC / 50.0f).coerceIn(0.1f, 1.0f)
                        LinearProgressIndicator(
                            progress = { thermalProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (telemetry.isThrottlingActive) Color.Red else emeraldAccent,
                            trackColor = if (isDark) Color.DarkGray.copy(alpha = 0.5f) else Color(0xFFE2E8F0)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) ElectricCyan else Color(0xFF2563EB),
                        contentColor = if (isDark) Color(0xFF003344) else Color.White
                    )
                ) {
                    Text("Tutup Diagnostik", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    unit: String,
    accentColor: Color,
    chipBg: Color,
    cardBorder: Color,
    textSecondaryColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = chipBg,
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = textSecondaryColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = accentColor
            )
            Text(
                text = unit,
                fontSize = 10.sp,
                color = textSecondaryColor
            )
        }
    }
}
