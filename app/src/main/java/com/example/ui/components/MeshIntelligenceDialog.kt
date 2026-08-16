package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.mesh.ComputeTier
import com.example.domain.mesh.MeshNetworkState
import com.example.domain.mesh.MeshPeerNode
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen

@Composable
fun MeshIntelligenceDialog(
    meshState: MeshNetworkState,
    onRefreshDiscovery: () -> Unit,
    onToggleMesh: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(ElectricCyan.copy(alpha = 0.6f), EmeraldGreen.copy(alpha = 0.2f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
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
                                .background(ElectricCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Jaringan Mesh AI",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Kecerdasan Kolektif P2P Antar-Perangkat",
                                fontSize = 11.sp,
                                color = EmeraldGreen
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Node Terhubung", fontSize = 11.sp, color = Color.LightGray)
                            Text("${meshState.connectedPeers.size} Perangkat", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
                        }
                        Divider(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Daya Swarm", fontSize = 11.sp, color = Color.LightGray)
                            Text("~${meshState.totalDistributedTops.toInt()} TOPS", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                        }
                        Divider(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Blok Pengetahuan", fontSize = 11.sp, color = Color.LightGray)
                            Text("${meshState.totalKnowledgeBlocksShared} Vektor", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sync Status Box
                Text(
                    text = "📡 Status: ${meshState.lastSyncMessage}",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Military Security Badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.08f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(EmeraldGreen, ElectricCyan)))
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
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "🛡️ Kubah Keamanan Militer Aktif (Zero-Knowledge)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                            Text(
                                text = "AES-256-GCM + HMAC-SHA384 | Anti-MitM & Anti-Poisoning Valid",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Perangkat di Sekitar (Wi-Fi Aware & BLE Mesh):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
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
                        MeshPeerCard(peer)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onRefreshDiscovery,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(ElectricCyan, EmeraldGreen)))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pindai Ulang", fontSize = 13.sp)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text("Tutup", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MeshPeerCard(peer: MeshPeerNode) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
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
                            ComputeTier.FLAGSHIP_NPU -> AmberWarning.copy(alpha = 0.2f)
                            ComputeTier.MIDRANGE_ACCELERATOR -> ElectricCyan.copy(alpha = 0.2f)
                            ComputeTier.ENTRY_CPU -> EmeraldGreen.copy(alpha = 0.2f)
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
                        ComputeTier.FLAGSHIP_NPU -> AmberWarning
                        ComputeTier.MIDRANGE_ACCELERATOR -> ElectricCyan
                        ComputeTier.ENTRY_CPU -> EmeraldGreen
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
                        color = Color.White
                    )
                    Text(
                        text = "${peer.signalStrengthDbm} dBm",
                        fontSize = 11.sp,
                        color = if (peer.signalStrengthDbm > -50) EmeraldGreen else Color.LightGray
                    )
                }

                Text(
                    text = "Model: ${peer.activeModel}",
                    fontSize = 11.sp,
                    color = ElectricCyan
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = peer.computeTier.label,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Berbagi: ${peer.sharedKnowledgeCount} Vektor",
                        fontSize = 10.sp,
                        color = AmberWarning
                    )
                }
            }
        }
    }
}
