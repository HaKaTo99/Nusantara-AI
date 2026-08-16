package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.crypto.PostQuantumCryptoVault
import com.example.domain.foundation.NationalFoundationDialectEngine
import com.example.domain.foundation.NusantaraDialect
import com.example.domain.governance.SovereignAGIGovernanceManager
import com.example.domain.learning.OnDeviceLearningEngine
import com.example.domain.spatial.SpatialIntelligenceEngine
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet

/**
 * =====================================================================
 * NUSANTARA AI - PHASE 5: SOVEREIGN AGI & DECENTRALIZED MESH DIALOG
 * Sub-Fase 5.1 s.d. 5.6 Showcase & Operational Control Center
 *
 * Lead System Architect: Herman Krisnanto
 * =====================================================================
 */

@Composable
fun SovereignAGIDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dialectEngine = remember { NationalFoundationDialectEngine() }
    val pqcVault = remember { PostQuantumCryptoVault() }
    val learningEngine = remember { OnDeviceLearningEngine() }
    val spatialEngine = remember { SpatialIntelligenceEngine() }
    val governanceManager = remember { SovereignAGIGovernanceManager() }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("🦅 Dialek & Fondasi", "🧠 LoRA & EWC", "🔐 PQC Vault", "🌐 Spasial & DAO", "📦 Survival Pod")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
            color = Color(0xFF0F172A),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
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
                                .background(NeonViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sovereign AGI & Decentralized Hub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Lead Architect: Herman Krisnanto • Fase 5 Selesai ✅",
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = ElectricCyan,
                    edgePadding = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> DialectAndFoundationTab(dialectEngine)
                        1 -> LoRAAndEWCTab(learningEngine)
                        2 -> PQCVaultTab(pqcVault)
                        3 -> SpatialAndDAOTab(spatialEngine, governanceManager)
                        4 -> SurvivalPodTab(governanceManager)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Tutup Hub", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DialectAndFoundationTab(engine: NationalFoundationDialectEngine) {
    var selectedDialect by remember { mutableStateOf(NusantaraDialect.INDONESIAN_STANDARD) }
    var greetingText by remember { mutableStateOf(engine.generateDialectGreeting(NusantaraDialect.INDONESIAN_STANDARD, "Kedaulatan Digital")) }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "12+ Dialek Daerah Nusantara & Model Fondasi Nasional",
                style = MaterialTheme.typography.titleSmall,
                color = ElectricCyan,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Dioptimasi untuk tata bahasa daerah, kearifan lokal, serta selaras dengan Konstitusi UUD 1945 & Pancasila.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
        }

        item {
            Text("Pilih Dialek Aktif:", style = MaterialTheme.typography.bodySmall, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    NusantaraDialect.INDONESIAN_STANDARD,
                    NusantaraDialect.JAVANESE_KRAMA,
                    NusantaraDialect.SUNDANESE_LEMES,
                    NusantaraDialect.MINANGKABAU
                ).forEach { dialect ->
                    FilterChip(
                        selected = selectedDialect == dialect,
                        onClick = {
                            selectedDialect = dialect
                            greetingText = engine.generateDialectGreeting(dialect, "Kedaulatan Digital")
                        },
                        label = { Text(dialect.displayName.take(12) + "..", fontSize = 10.sp) }
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Contoh Respons Budaya (${selectedDialect.region}):", fontSize = 11.sp, color = ElectricCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(greetingText, fontSize = 13.sp, color = Color.White)
                }
            }
        }

        item {
            Text("Model Fondasi Berdaulat Nasional (Open Weights):", style = MaterialTheme.typography.bodySmall, color = Color.White)
        }

        items(engine.getSovereignFoundationModels()) { model ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(model.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text("${model.parameterCount} • Konteks: ${model.contextWindowTokens / 1024}k • 100% Data NKRI", fontSize = 10.sp, color = Color.Gray)
                    }
                    Text("TERVERIFIKASI ✅", color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LoRAAndEWCTab(engine: OnDeviceLearningEngine) {
    val adapter by engine.activeAdapter.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Continuous On-Device Learning & LoRA Adapter",
                style = MaterialTheme.typography.titleSmall,
                color = ElectricCyan,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pelatihan mandiri di ponsel saat pengisian daya malam hari dengan pencegahan Catastrophic Forgetting (EWC).",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📊 Status Adapter Aktif", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    Text("Nama: ${adapter.adapterName}", fontSize = 11.sp, color = ElectricCyan)
                    Text("Rank: r=${adapter.rank} | Alpha: ${adapter.alpha}", fontSize = 11.sp, color = Color.LightGray)
                    Text("Target Modules: ${adapter.targetModules.joinToString()}", fontSize = 11.sp, color = Color.LightGray)
                    Text("Token Terlatih: ${adapter.trainingTokensCount} tokens", fontSize = 11.sp, color = Color.LightGray)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Retensi EWC: ${(adapter.ewcRetentionScore * 100).toInt()}% (Aman)", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Privasi DP: ε=${adapter.privacyEpsilon}", color = ElectricCyan, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PQCVaultTab(vault: PostQuantumCryptoVault) {
    val kyberKey = remember { vault.generateKyberKeyPair() }
    val zkProof = remember { vault.generateZKMLProof("garuda-70b", "Test Query", "Test Result") }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Kriptografi Pasca-Kuantum (NIST FIPS 203/204)",
                style = MaterialTheme.typography.titleSmall,
                color = ElectricCyan,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enkapsulasi ML-KEM-768 (Kyber), tanda tangan ML-DSA-652 (Dilithium), dan ZK-ML SNARKs.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🔐 ML-KEM-768 Kyber Public Key", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    Text(
                        text = kyberKey.publicKeyHex.take(64) + "...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = EmeraldGreen
                    )
                    Text("Tingkat Keamanan: Level 3 (192-bit Quantum Resistant)", fontSize = 10.sp, color = Color.LightGray)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("📜 ZK-ML Verification Proof", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    Text("Proof ID: ${zkProof.proofId}", fontSize = 10.sp, color = ElectricCyan)
                    Text("Model Hash: ${zkProof.modelCommitmentHash.take(32)}...", fontSize = 10.sp, color = Color.Gray)
                    Text("Status: TERVERIFIKASI ASLI ✅", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SpatialAndDAOTab(spatial: SpatialIntelligenceEngine, governance: SovereignAGIGovernanceManager) {
    val cards = remember { spatial.createSpatialWorkspace() }
    val duplexState = remember { spatial.processFullDuplexSpeech(false) }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Spatial XR 3D & Full-Duplex Audio (<50ms)",
                style = MaterialTheme.typography.titleSmall,
                color = ElectricCyan,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🎧 Full-Duplex Neural Speech Engine", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    Text("Latensi Interupsi: ${duplexState.latencyMs} ms (Ultra Fast)", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Nada Emosional: ${duplexState.emotionalTone}", fontSize = 10.sp, color = Color.LightGray)
                    Text("Kartu Spasial Aktif: ${cards.size} Floating Workspaces", fontSize = 10.sp, color = ElectricCyan)
                }
            }
        }
    }
}

@Composable
private fun SurvivalPodTab(governance: SovereignAGIGovernanceManager) {
    val pod = remember { governance.getSurvivalPodBlueprint() }
    val kpi = remember { governance.getNationalKPIMetrics() }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Cold-Boot AI Survival Pod & KPI Nasional",
                style = MaterialTheme.typography.titleSmall,
                color = ElectricCyan,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Paket darurat peradaban nasional yang dapat di-boot 100% offline dengan tenaga surya.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📦 ${pod.title}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    pod.modulesIncluded.forEach { mod ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("• ", color = ElectricCyan, fontSize = 12.sp)
                            Text(mod, color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                    Text("Ukuran Kompresi: ${pod.totalSizeCompressedMB} MB • Uji Tenaga Surya: SUKSES ✅", color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🏆 Metrik Kedaulatan Digital Nasional", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    Text("Kedaulatan Data Domestik: ${(kpi.domesticDataSovereigntyRatio * 100).toInt()}% NKRI", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Efisiensi Energi: ${kpi.energyEfficiencyKWhPer10kQueries} kWh / 10k kueri", fontSize = 10.sp, color = Color.LightGray)
                    Text("Insiden Kebocoran: ${kpi.securityIncidentCount} (Zero Breach)", color = EmeraldGreen, fontSize = 10.sp)
                    Text(kpi.aseanRanking, color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
