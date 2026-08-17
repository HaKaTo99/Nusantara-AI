package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ai.DebateMessage
import com.example.domain.ai.DebateState
import com.example.domain.ai.FlowDebateEngine
import com.example.domain.ai.OfflineReasoningEngine
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonViolet
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Composable
fun FlowDebateScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val debateEngine = remember {
        FlowDebateEngine(
            offlineEngine = OfflineReasoningEngine,
            analyticsDao = null
        )
    }

    var debateTopic by remember {
        mutableStateOf("Apakah On-Device AI akan menggantikan Private Cloud AI dalam 3 tahun ke depan?")
    }
    var isDebating by remember { mutableStateOf(false) }
    var totalRounds by remember { mutableIntStateOf(3) }
    var currentRound by remember { mutableIntStateOf(0) }
    var synthesisComplete by remember { mutableStateOf(false) }
    val debateMessages = remember { mutableStateListOf<DebateMessage>() }
    val listState = rememberLazyListState()

    val presetTopics = listOf(
        "Apakah On-Device AI akan menggantikan Cloud AI?",
        "Etika AI: Regulasi ketat vs Inovasi terbuka",
        "Keamanan E2EE vs Aksesibilitas Metadata",
        "Arsitektur Monolitik vs Microservices 2026",
        "AGI akan mengancam atau meningkatkan kualitas kerja manusia?"
    )

    fun startDebate() {
        if (debateTopic.isBlank() || isDebating) return
        isDebating = true
        synthesisComplete = false
        currentRound = 0
        debateMessages.clear()

        coroutineScope.launch {
            debateEngine.startDebate(
                topic = debateTopic,
                totalRounds = totalRounds
            ).catch { e ->
                debateMessages.add(
                    DebateMessage(
                        speakerName = "Sistem",
                        speakerEmoji = "⚠️",
                        roleBadge = "ERROR",
                        content = "Terjadi kesalahan: ${e.message}",
                        round = 0
                    )
                )
                isDebating = false
            }.collect { state ->
                when (state) {
                    is DebateState.RoundStarted -> {
                        currentRound = state.round
                    }
                    is DebateState.ArgumentEmitted -> {
                        debateMessages.add(state.message)
                        // Auto-scroll ke bawah
                        listState.animateScrollToItem(debateMessages.size - 1)
                    }
                    is DebateState.SynthesisComplete -> {
                        synthesisComplete = true
                        isDebating = false
                    }
                    is DebateState.Error -> {
                        isDebating = false
                    }
                    else -> {}
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NeonViolet.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon_CompareArrows()
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Arena Debat Multi-AI",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Model AI berargumen & memecahkan dilema kompleks secara otonom",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Preset Topic Chips ───────────────────────────────────────────
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(presetTopics) { topic ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (debateTopic == topic) NeonViolet.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = if (debateTopic == topic) 1.dp else 0.dp,
                            color = if (debateTopic == topic) NeonViolet else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { debateTopic = topic }
                ) {
                    Text(
                        text = topic,
                        fontSize = 11.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── Topic Input ──────────────────────────────────────────────────
        OutlinedTextField(
            value = debateTopic,
            onValueChange = { debateTopic = it },
            label = { Text("Topik Debat atau Masalah Kompleks") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("debate_topic_input"),
            shape = RoundedCornerShape(12.dp),
            maxLines = 2,
            enabled = !isDebating
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Round Slider ─────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Putaran:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(64.dp)
            )
            Slider(
                value = totalRounds.toFloat(),
                onValueChange = { totalRounds = it.toInt() },
                valueRange = 1f..5f,
                steps = 3,
                enabled = !isDebating,
                colors = SliderDefaults.colors(thumbColor = NeonViolet, activeTrackColor = NeonViolet),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$totalRounds putaran",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonViolet,
                modifier = Modifier.width(72.dp)
            )
        }

        // ── Progress Bar saat debat berjalan ────────────────────────────
        AnimatedVisibility(visible = isDebating) {
            Column {
                LinearProgressIndicator(
                    progress = { if (totalRounds > 0) (currentRound.toFloat() / (totalRounds + 1)) else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(50))
                        .height(4.dp),
                    color = NeonViolet,
                    strokeCap = StrokeCap.Round
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (currentRound <= totalRounds) "Putaran $currentRound dari $totalRounds..."
                           else "Menyusun sintesis akhir...",
                    fontSize = 10.sp,
                    color = NeonViolet
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Action Buttons ───────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { startDebate() },
                enabled = !isDebating && debateTopic.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                modifier = Modifier
                    .weight(1f)
                    .testTag("start_debate_button")
            ) {
                if (isDebating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sedang Berdebat...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    androidx.compose.material3.Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mulai Debat AI", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            if (debateMessages.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        debateMessages.clear()
                        synthesisComplete = false
                        currentRound = 0
                    },
                    enabled = !isDebating
                ) {
                    androidx.compose.material3.Icon(
                        Icons.Default.Stop,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Debate Stream ────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(debateMessages) { msg ->
                DebateMessageCard(message = msg)
            }

            // Synthesis Complete Banner
            if (synthesisComplete) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = EmeraldGreen.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏆", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Debat Selesai",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                                Text(
                                    text = "Konsensus AI telah dicapai dalam $totalRounds putaran",
                                    fontSize = 11.sp,
                                    color = EmeraldGreen.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun Icon_CompareArrows() {
    androidx.compose.material3.Icon(
        imageVector = Icons.AutoMirrored.Filled.CompareArrows,
        contentDescription = null,
        tint = NeonViolet,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun DebateMessageCard(message: DebateMessage) {
    val borderColor = when (message.roleBadge) {
        "PRO"       -> ElectricCyan.copy(alpha = 0.35f)
        "KONTRA"    -> NeonViolet.copy(alpha = 0.35f)
        "MODERATOR" -> EmeraldGreen.copy(alpha = 0.5f)
        else        -> Color.Transparent
    }
    val bgColor = when (message.roleBadge) {
        "MODERATOR" -> EmeraldGreen.copy(alpha = 0.08f)
        else        -> MaterialTheme.colorScheme.surfaceVariant
    }
    val badgeColor = when (message.roleBadge) {
        "PRO"       -> ElectricCyan
        "KONTRA"    -> NeonViolet
        "MODERATOR" -> EmeraldGreen
        else        -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (message.isSynthesis) 1.5.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = message.speakerEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = message.speakerName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
                // Role badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (message.isSynthesis) "⚖️ SINTESIS" else message.roleBadge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = message.content,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Putaran ${message.round}",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
