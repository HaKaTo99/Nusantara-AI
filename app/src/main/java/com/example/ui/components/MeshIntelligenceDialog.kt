package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.mesh.ComputeTier
import com.example.domain.mesh.MeshNetworkState
import com.example.domain.mesh.MeshPeerNode
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet

@Composable
fun MeshIntelligenceDialog(
    meshState: MeshNetworkState,
    onRefreshDiscovery: () -> Unit,
    onToggleMesh: (Boolean) -> Unit,
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
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = cardBorder,
                    shape = RoundedCornerShape(24.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = cardBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(primaryAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Jaringan Mesh AI",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimaryColor
                            )
                            Text(
                                text = "Kecerdasan Kolektif P2P Antar-Perangkat",
                                fontSize = 11.sp,
                                color = emeraldAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = textSecondaryColor)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = chipBg),
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Node Terhubung", fontSize = 11.sp, color = textSecondaryColor)
                            Text("${meshState.connectedPeers.size} Perangkat", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryAccent)
                        }
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.dp)
                                .background(cardBorder)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Daya Swarm", fontSize = 11.sp, color = textSecondaryColor)
                            Text("~${meshState.totalDistributedTops.toInt()} TOPS", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isDark) AmberWarning else Color(0xFFB45309))
                        }
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.dp)
                                .background(cardBorder)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Blok Pengetahuan", fontSize = 11.sp, color = textSecondaryColor)
                            Text("${meshState.totalKnowledgeBlocksShared} Vektor", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = emeraldAccent)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sync Status Box
                Text(
                    text = "📡 Status: ${meshState.lastSyncMessage}",
                    fontSize = 12.sp,
                    color = textSecondaryColor,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Military Security Badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) emeraldAccent.copy(alpha = 0.1f) else Color(0xFFECFDF5)),
                    border = BorderStroke(1.dp, if (isDark) emeraldAccent.copy(alpha = 0.3f) else Color(0xFFA7F3D0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = emeraldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "🛡️ Kubah Keamanan Militer Aktif (Zero-Knowledge)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = emeraldAccent
                            )
                            Text(
                                text = "AES-256-GCM + HMAC-SHA384 | Anti-MitM & Anti-Poisoning Valid",
                                fontSize = 10.sp,
                                color = textSecondaryColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Perangkat di Sekitar (Wi-Fi Aware & BLE Mesh):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimaryColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Peer List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(meshState.connectedPeers) { peer ->
                        MeshPeerCard(peer, isDark, textPrimaryColor, textSecondaryColor, primaryAccent, emeraldAccent, chipBg, cardBorder)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRefreshDiscovery,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                            contentColor = primaryAccent
                        ),
                        border = BorderStroke(1.dp, primaryAccent)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pindai Ulang", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) EmeraldGreen else Color(0xFF047857),
                            contentColor = if (isDark) Color(0xFF003344) else Color.White
                        )
                    ) {
                        Text("Tutup", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MeshPeerCard(
    peer: MeshPeerNode,
    isDark: Boolean = true,
    textPrimaryColor: Color = Color(0xFFF8FAFC),
    textSecondaryColor: Color = Color(0xFF94A3B8),
    primaryAccent: Color = ElectricCyan,
    emeraldAccent: Color = EmeraldGreen,
    chipBg: Color = Color(0xFF131D31),
    cardBorder: Color = Color(0xFF223147)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = chipBg),
        border = BorderStroke(1.dp, cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (peer.computeTier) {
                            ComputeTier.FLAGSHIP_NPU -> (if (isDark) AmberWarning else Color(0xFFD97706)).copy(alpha = 0.2f)
                            ComputeTier.MIDRANGE_ACCELERATOR -> primaryAccent.copy(alpha = 0.2f)
                            ComputeTier.ENTRY_CPU -> emeraldAccent.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (peer.computeTier) {
                        ComputeTier.FLAGSHIP_NPU -> Icons.Default.Speed
                        ComputeTier.MIDRANGE_ACCELERATOR -> Icons.Default.Build
                        ComputeTier.ENTRY_CPU -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = when (peer.computeTier) {
                        ComputeTier.FLAGSHIP_NPU -> if (isDark) AmberWarning else Color(0xFFD97706)
                        ComputeTier.MIDRANGE_ACCELERATOR -> primaryAccent
                        ComputeTier.ENTRY_CPU -> emeraldAccent
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = peer.deviceName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimaryColor
                    )
                    Text(
                        text = "${peer.signalStrengthDbm} dBm",
                        fontSize = 11.sp,
                        color = if (peer.signalStrengthDbm > -50) emeraldAccent else textSecondaryColor
                    )
                }

                Text(
                    text = "Model: ${peer.activeModel}",
                    fontSize = 11.sp,
                    color = primaryAccent,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = peer.computeTier.label,
                        fontSize = 10.sp,
                        color = textSecondaryColor
                    )
                    Text(
                        text = "Berbagi: ${peer.sharedKnowledgeCount} Vektor",
                        fontSize = 10.sp,
                        color = if (isDark) AmberWarning else Color(0xFFD97706),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
