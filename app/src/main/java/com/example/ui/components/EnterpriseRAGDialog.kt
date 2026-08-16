package com.example.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.agent.DAGSubTask
import com.example.domain.agent.SwarmWorkflowState
import com.example.domain.agent.TaskStatus
import com.example.domain.enterprise.EnterpriseGatewayState
import com.example.domain.enterprise.EnterpriseROISummary
import com.example.domain.enterprise.GovTechDocumentTemplate
import com.example.domain.rag.KnowledgeBaseStats
import com.example.domain.rag.RAGSearchResult
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen

/**
 * =====================================================================
 * NUSANTARA AI - PHASE 4: ENTERPRISE RAG & SWARM ORCHESTRATION DIALOG
 * Lead System Architect: Herman Krisnanto
 *
 * Provides tabbed interface for:
 * 1. Local Vector RAG Knowledge Base & Semantic Search
 * 2. Multi-Agent DAG Swarm Workflow Orchestration
 * 3. Indonesian GovTech & e-Faktur Pajak Compliance Hub
 * 4. Private Sovereign Gateway & Zero-Knowledge Device Sync
 * =====================================================================
 */

@Composable
fun EnterpriseRAGDialog(
    ragStats: KnowledgeBaseStats,
    searchResults: List<RAGSearchResult>,
    swarmState: SwarmWorkflowState,
    roiSummary: EnterpriseROISummary,
    gatewayState: EnterpriseGatewayState,
    templates: List<GovTechDocumentTemplate>,
    onSearchQuery: (String) -> Unit,
    onIngestSampleDoc: () -> Unit,
    onStartSwarmWorkflow: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("📚 Vektor RAG", "🐝 Swarm DAG", "🏢 GovTech", "🔒 Kluster")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
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
                    .padding(18.dp)
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
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(ElectricCyan.copy(alpha = 0.3f), EmeraldGreen.copy(alpha = 0.3f)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Enterprise Hub",
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Enterprise & Swarm Hub",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Fase 4 • Lead Architect: Herman Krisnanto",
                                color = ElectricCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Selector
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceVariant,
                    contentColor = ElectricCyan,
                    edgePadding = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) ElectricCyan else Color.White.copy(alpha = 0.6f)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Contents
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> VectorRAGTab(
                            stats = ragStats,
                            searchResults = searchResults,
                            onSearchQuery = onSearchQuery,
                            onIngestSampleDoc = onIngestSampleDoc
                        )
                        1 -> SwarmDAGTab(
                            swarmState = swarmState,
                            onStartSwarm = onStartSwarmWorkflow
                        )
                        2 -> GovTechComplianceTab(
                            templates = templates,
                            roiSummary = roiSummary
                        )
                        3 -> PrivateGatewayTab(
                            gatewayState = gatewayState
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: VECTOR RAG & KNOWLEDGE BASE
// -------------------------------------------------------------
@Composable
private fun VectorRAGTab(
    stats: KnowledgeBaseStats,
    searchResults: List<RAGSearchResult>,
    onSearchQuery: (String) -> Unit,
    onIngestSampleDoc: () -> Unit
) {
    var queryText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Stats Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📊 Status Basis Pengetahuan Vektor",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "sqlite-vec ON-DEVICE",
                                color = EmeraldGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem(label = "Dokumen", value = "${stats.totalDocuments}")
                        StatItem(label = "Segmen Chunk", value = "${stats.totalChunks}")
                        StatItem(label = "Vektor Float", value = "${stats.totalVectors}")
                        StatItem(label = "Kripto TEE", value = if (stats.isEncrypted) "AKTIF 🔒" else "NONAKTIF")
                    }
                }
            }
        }

        // Action Ingest & Search Input
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = queryText,
                    onValueChange = {
                        queryText = it
                        onSearchQuery(it)
                    },
                    placeholder = { Text("Cari semantik dokumen...", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onIngestSampleDoc,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan.copy(alpha = 0.2f))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Ingest", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ingest", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Results List
        if (searchResults.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (queryText.isBlank()) "Ketik kueri di atas untuk pencarian semantik hibrida (Cosine + BM25)." else "Tidak ada dokumen yang cocok dengan kueri.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(searchResults) { result ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📄 ${result.chunk.documentTitle} [Segmen #${result.chunk.chunkIndex + 1}]",
                                color = ElectricCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (result.hybridScore >= 0.7f) EmeraldGreen.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = result.matchConfidence,
                                    color = if (result.hybridScore >= 0.7f) EmeraldGreen else AmberWarning,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = result.chunk.text,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Vektor: ${(result.vectorScore * 100).toInt()}% • BM25: ${(result.keywordScore * 100).toInt()}%",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 10.sp
                            )
                            Text(
                                text = "${result.chunk.tokenCount} tokens",
                                color = Color.White.copy(alpha = 0.4f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: SWARM DAG TASK ORCHESTRATOR
// -------------------------------------------------------------
@Composable
private fun SwarmDAGTab(
    swarmState: SwarmWorkflowState,
    onStartSwarm: (String) -> Unit
) {
    var goalText by remember { mutableStateOf("Analisis Kesiapan Kedaulatan AI & Kepatuhan UU PDP untuk BUMN") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Goal input
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🎯 Sasaran Strategis Multi-Agen DAG",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = goalText,
                        onValueChange = { goalText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onStartSwarm(goalText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !swarmState.isRunning,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run Swarm", tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (swarmState.isRunning) "Sedang Mengeksekusi DAG..." else "Eksekusi Swarm Multi-Agen",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Progress Bar
        if (swarmState.isRunning || swarmState.tasks.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tahapan: ${swarmState.currentStage}",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(swarmState.progressPercent * 100).toInt()}%",
                                color = EmeraldGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { swarmState.progressPercent },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = ElectricCyan,
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }

        // DAG Tasks Breakdown
        items(swarmState.tasks) { task ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TaskStatusBadge(task.status)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "👤 ${task.assignedAgentRole}",
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (task.output.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = task.output,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Final Synthesis Report
        if (swarmState.finalSynthesis.isNotBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, EmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Done", tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sintesis Eksekutif Lead Architect",
                                color = EmeraldGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = swarmState.finalSynthesis,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: GOVTECH & E-FAKTUR COMPLIANCE
// -------------------------------------------------------------
@Composable
private fun GovTechComplianceTab(
    templates: List<GovTechDocumentTemplate>,
    roiSummary: EnterpriseROISummary
) {
    var selectedTemplate by remember { mutableStateOf<GovTechDocumentTemplate?>(templates.firstOrNull()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ROI Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📈 Metrik Efisiensi Jam Kerja & ROI",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem(label = "Tugas Otomasi", value = "${roiSummary.totalTasksAutomated}")
                        StatItem(label = "Jam Dihemat", value = "${roiSummary.totalHoursSaved}h")
                        StatItem(label = "Nilai Efisiensi", value = "Rp 39.6jt")
                        StatItem(label = "SLA Uptime", value = "${roiSummary.systemUptimeSLA}%")
                    }
                }
            }
        }

        // Template List
        item {
            Text(
                text = "📑 Template Tata Naskah Dinas & Pajak Resmi",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(templates) { tpl ->
            val isSelected = selectedTemplate?.id == tpl.id
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) ElectricCyan.copy(alpha = 0.15f) else DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedTemplate = tpl }
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) ElectricCyan else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tpl.title,
                            color = if (isSelected) ElectricCyan else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = tpl.category,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚖️ ${tpl.legalStandard}",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Preview of selected template
        selectedTemplate?.let { tpl ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Pratinjau Format:",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = tpl.templateBody,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: PRIVATE CLUSTER & ZERO-KNOWLEDGE MULTI-DEVICE
// -------------------------------------------------------------
@Composable
private fun PrivateGatewayTab(
    gatewayState: EnterpriseGatewayState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Active Cluster Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏛️ Kluster Sovereign Aktif",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "TERHUBUNG ✅",
                                color = EmeraldGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = gatewayState.activeCluster.clusterName,
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "🌐 Endpoint: ${gatewayState.activeCluster.endpointUrl}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "📍 Lokasi: ${gatewayState.activeCluster.location}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "🔒 Protokol: ${gatewayState.activeCluster.tlsVersion}",
                        color = EmeraldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Paired Devices List (Zero-Knowledge)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📱 Perangkat Tertaut (Zero-Knowledge ECDH)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${gatewayState.pairedDevices.size} Node",
                    color = ElectricCyan,
                    fontSize = 11.sp
                )
            }
        }

        items(gatewayState.pairedDevices) { dev ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = dev.deviceName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kunci: ${dev.ecdhPublicKeyHash} • ${dev.deviceType}",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = dev.syncStatus,
                            color = EmeraldGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER SUB-COMPONENTS
// -------------------------------------------------------------
@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
    }
}

@Composable
private fun TaskStatusBadge(status: TaskStatus) {
    val (bg, fg, label) = when (status) {
        TaskStatus.PENDING -> Triple(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.6f), "PENDING")
        TaskStatus.EXECUTING -> Triple(ElectricCyan.copy(alpha = 0.2f), ElectricCyan, "EKSEKUSI ⚡")
        TaskStatus.CRITIQUE_REVIEW -> Triple(AmberWarning.copy(alpha = 0.2f), AmberWarning, "REVIEW 🔍")
        TaskStatus.COMPLETED -> Triple(EmeraldGreen.copy(alpha = 0.2f), EmeraldGreen, "SELESAI ✅")
        TaskStatus.FAILED -> Triple(Color.Red.copy(alpha = 0.2f), Color.Red, "GAGAL ❌")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
