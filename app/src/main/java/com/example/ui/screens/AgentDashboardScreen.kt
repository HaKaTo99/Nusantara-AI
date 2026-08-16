package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AgentEntity
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val agents by viewModel.agents.collectAsState()
    var showCreateSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val runningCount = agents.count { it.status == "RUNNING" }
    val completedCount = agents.count { it.status == "COMPLETED" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF059669).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Agen AI 24/7",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$runningCount berjalan · $completedCount selesai · ${agents.size} total",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                FilledTonalIconButton(onClick = { showCreateSheet = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Buat Agen Baru")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatChip(
                    emoji = "🟢",
                    label = "Berjalan",
                    value = "$runningCount",
                    containerColor = Color(0xFF059669).copy(alpha = 0.12f),
                    textColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    emoji = "✅",
                    label = "Selesai",
                    value = "$completedCount",
                    containerColor = Color(0xFF1D4ED8).copy(alpha = 0.12f),
                    textColor = Color(0xFF60A5FA),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    emoji = "⏸️",
                    label = "Idle",
                    value = "${agents.count { it.status == "IDLE" }}",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // ── Agent List ───────────────────────────────────────────────────
        if (agents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🤖", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Belum ada agen aktif",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Buat agen AI pertama Anda untuk\notomasi tugas berulang 24/7",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = { showCreateSheet = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buat Agen Pertama")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(agents, key = { it.id }) { agent ->
                    AgentCard(
                        agent = agent,
                        onStartStop = {
                            viewModel.toggleAgent(agent)
                        },
                        onDelete = {
                            viewModel.deleteAgent(agent.id)
                        }
                    )
                }
            }
        }
    }

    // ── Create Agent Bottom Sheet ────────────────────────────────────
    if (showCreateSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCreateSheet = false },
            sheetState = sheetState
        ) {
            CreateAgentSheet(
                onCancel = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showCreateSheet = false
                    }
                },
                onCreate = { name, description, emoji, taskType ->
                    viewModel.createAgent(
                        name = name,
                        description = description,
                        avatarEmoji = emoji,
                        taskType = taskType
                    )
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showCreateSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun AgentCard(
    agent: AgentEntity,
    onStartStop: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (agent.status) {
        "RUNNING"   -> Color(0xFF10B981)
        "COMPLETED" -> Color(0xFF60A5FA)
        "ERROR"     -> Color(0xFFEF4444)
        "PAUSED"    -> Color(0xFFF59E0B)
        else        -> Color(0xFF94A3B8)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (agent.status == "RUNNING") 1.dp else 0.dp,
                color = if (agent.status == "RUNNING") Color(0xFF10B981).copy(alpha = 0.4f)
                        else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = agent.avatarEmoji, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = agent.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Text(
                                text = when (agent.status) {
                                    "RUNNING"   -> "Berjalan"
                                    "COMPLETED" -> "Selesai"
                                    "ERROR"     -> "Error"
                                    "PAUSED"    -> "Dijeda"
                                    else        -> "Idle"
                                },
                                fontSize = 11.sp,
                                color = statusColor
                            )
                            Text(
                                text = "· ${agent.tasksCompleted} tugas selesai",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Row {
                    IconButton(onClick = onStartStop, modifier = Modifier.size(36.dp)) {
                        AnimatedContent(
                            targetState = agent.status,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "agent_action"
                        ) { status ->
                            Icon(
                                imageVector = if (status == "RUNNING") Icons.Default.PauseCircle
                                              else Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Hapus Agen",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Progress bar jika ada progress
            if (agent.status == "RUNNING" || agent.progress > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                val progress by animateFloatAsState(
                    targetValue = agent.progress / 100f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "agent_progress"
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50)),
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${agent.progress}% — ${agent.description.take(60)}...",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = agent.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun StatChip(
    emoji: String,
    label: String,
    value: String,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$emoji $value", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
            Text(text = label, fontSize = 10.sp, color = textColor.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun CreateAgentSheet(
    onCancel: () -> Unit,
    onCreate: (name: String, description: String, emoji: String, taskType: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🤖") }
    var selectedTaskType by remember { mutableStateOf("GENERAL") }

    val taskTypes = listOf(
        "GENERAL" to "🤖 Umum",
        "EMAIL" to "📧 Email",
        "CALENDAR" to "📅 Kalender",
        "REPORT" to "📊 Laporan",
        "RESEARCH" to "🔬 Riset",
        "MONITOR" to "📡 Monitor"
    )

    val emojiOptions = listOf("🤖", "📧", "📅", "📊", "🔬", "📡", "💼", "🛡️", "🌐", "⚡")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Buat Agen AI Baru",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Agen berjalan otomatis sesuai jadwal Anda",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Emoji picker
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            emojiOptions.forEach { emoji ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (emoji == selectedEmoji) Color(0xFF00F2FE).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (emoji == selectedEmoji) 1.5.dp else 0.dp,
                            color = if (emoji == selectedEmoji) Color(0xFF00F2FE)
                                    else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { selectedEmoji = emoji },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Agen") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi tugas agen") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Task type chips
        Text(
            text = "Tipe Tugas",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            taskTypes.take(3).forEach { (type, label) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (type == selectedTaskType) Color(0xFF00F2FE).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (type == selectedTaskType) 1.dp else 0.dp,
                            color = if (type == selectedTaskType) Color(0xFF00F2FE)
                                    else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedTaskType = type }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            taskTypes.drop(3).forEach { (type, label) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (type == selectedTaskType) Color(0xFF00F2FE).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (type == selectedTaskType) 1.dp else 0.dp,
                            color = if (type == selectedTaskType) Color(0xFF00F2FE)
                                    else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedTaskType = type }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) { Text("Batal") }

            Button(
                onClick = {
                    if (name.isNotBlank() && description.isNotBlank()) {
                        onCreate(name, description, selectedEmoji, selectedTaskType)
                    }
                },
                enabled = name.isNotBlank() && description.isNotBlank(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Buat Agen")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
