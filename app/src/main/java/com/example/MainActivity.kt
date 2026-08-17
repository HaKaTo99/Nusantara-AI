package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.components.DiagnosticsDialog
import com.example.ui.components.ModelSelectorDialog
import com.example.ui.components.SecurityBadgeDialog
import com.example.ui.components.TopAppBarWithStatus
import com.example.ui.screens.AgentDashboardScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.FlowDebateScreen
import com.example.ui.screens.MultimodalScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ToolsAndPersonaScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

enum class AppDestination(val label: String, val icon: ImageVector, val tag: String) {
    CHAT("Obrolan", Icons.Default.ChatBubble, "nav_chat"),
    MULTIMODAL("Studio", Icons.Default.Image, "nav_multimodal"),
    TOOLS("Alat & Agen", Icons.Default.SmartToy, "nav_tools"),
    ANALYTICS("Sistem", Icons.Default.Insights, "nav_analytics"),
    DEBATE("Debat", Icons.Default.ChatBubble, "nav_debate"),
    AGENTS("Agen AI", Icons.Default.SmartToy, "nav_agents"),
    SETTINGS("Pengaturan", Icons.Default.Settings, "nav_settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("nusantara_prefs", Context.MODE_PRIVATE)
                val onboardingDone = prefs.getBoolean("onboarding_complete", false)

                var showOnboarding by remember { mutableStateOf(!onboardingDone) }

                if (showOnboarding) {
                    OnboardingScreen(
                        onFinish = { startOffline ->
                            prefs.edit().putBoolean("onboarding_complete", true).apply()
                            if (startOffline) {
                                viewModel.setModelAndMode(viewModel.selectedModel.value, "OFFLINE")
                            }
                            showOnboarding = false
                        }
                    )
                } else {
                    NusantaraApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NusantaraApp(viewModel: MainViewModel) {
    var currentDestination by remember { mutableStateOf(AppDestination.CHAT) }
    var showModelDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showDiagnosticsDialog by remember { mutableStateOf(false) }
    var showMeshDialog by remember { mutableStateOf(false) }
    var showEnterpriseDialog by remember { mutableStateOf(false) }
    var showSovereignAGIDialog by remember { mutableStateOf(false) }

    val isOnline by viewModel.isOnline.collectAsState()
    val modePreference by viewModel.modePreference.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val meshState by viewModel.meshState.collectAsState()

    val isProcessing by viewModel.isProcessing.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val voiceAmplitude by viewModel.voiceAmplitude.collectAsState()

    val messages by viewModel.messages.collectAsState()
    val personas by viewModel.personas.collectAsState()
    val activePersona by viewModel.activePersona.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val runningAgentCount by viewModel.runningAgentCount.collectAsState()

    val totalTokens by viewModel.totalTokens.collectAsState()
    val avgLatency by viewModel.avgLatency.collectAsState()
    val offlineCount by viewModel.offlineCount.collectAsState()
    val onlineCount by viewModel.onlineCount.collectAsState()
    val recentLogs by viewModel.recentLogs.collectAsState()

    // Phase 3 State collections
    val hubModels by viewModel.hubModels.collectAsState()
    val telemetrySnapshot by viewModel.telemetrySnapshot.collectAsState()

    // Phase 4 State collections
    val ragStats by viewModel.ragStats.collectAsState()
    val ragSearchResults by viewModel.ragSearchResults.collectAsState()
    val swarmWorkflowState by viewModel.swarmWorkflowState.collectAsState()
    val enterpriseROISummary by viewModel.enterpriseROISummary.collectAsState()
    val enterpriseGatewayState by viewModel.enterpriseGatewayState.collectAsState()

    // Navigation items — 4 Essential Tabs
    val navItems = listOf(
        AppDestination.CHAT,
        AppDestination.MULTIMODAL,
        AppDestination.TOOLS,
        AppDestination.ANALYTICS
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithStatus(
                isOnline = isOnline,
                modePreference = modePreference,
                selectedModel = selectedModel,
                syncState = syncState,
                onModelClick = { showModelDialog = true },
                onSecurityClick = { showSecurityDialog = true },
                onDiagnosticsClick = { showDiagnosticsDialog = true },
                onMeshClick = { showMeshDialog = true },
                onEnterpriseClick = { showEnterpriseDialog = true },
                onSovereignAGIClick = { showSovereignAGIDialog = true },
                onSyncClick = { viewModel.triggerSync() },
                onSettingsClick = {
                    currentDestination = if (currentDestination == AppDestination.SETTINGS)
                        AppDestination.CHAT
                    else AppDestination.SETTINGS
                }
            )
        },
        bottomBar = {
            val isDark = MaterialTheme.colorScheme.background.red < 0.5f
            val navPrimary = if (isDark) ElectricCyan else Color(0xFF0F52BA)
            val navInactive = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

            NavigationBar(
                containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color(0xFFFFFFFF),
                tonalElevation = if (isDark) 3.dp else 1.dp,
                modifier = Modifier.border(
                    width = if (isDark) 0.dp else 1.dp,
                    color = if (isDark) Color.Transparent else Color(0xFFE2E8F0)
                )
            ) {
                navItems.forEach { destination ->
                    val selected = currentDestination == destination
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = navPrimary,
                            selectedTextColor = navPrimary,
                            indicatorColor = if (isDark) ElectricCyan.copy(alpha = 0.2f) else Color(0xFFEFF6FF),
                            unselectedIconColor = navInactive,
                            unselectedTextColor = navInactive
                        ),
                        modifier = Modifier.testTag(destination.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    AppDestination.CHAT -> {
                        ChatScreen(
                            messages = messages,
                            activePersona = activePersona,
                            isProcessing = isProcessing,
                            isListening = isListening,
                            voiceAmplitude = voiceAmplitude,
                            onSendMessage = { viewModel.sendMessage(it) },
                            onStartVoice = { viewModel.startVoiceInput() },
                            onStopVoice = { viewModel.stopVoiceInput() },
                            onSpeakText = { viewModel.speakText(it) },
                            onNewChat = { viewModel.startNewChat() }
                        )
                    }

                    AppDestination.MULTIMODAL -> {
                        MultimodalScreen(
                            documents = documents,
                            onAnalyzeImagePrompt = { viewModel.analyzeImagePrompt(it) },
                            onProcessDocument = { title, type, content ->
                                viewModel.processDocument(title, type, content)
                            }
                        )
                    }

                    AppDestination.DEBATE -> {
                        FlowDebateScreen(viewModel = viewModel)
                    }

                    AppDestination.TOOLS -> {
                        ToolsAndPersonaScreen(
                            personas = personas,
                            activePersonaId = activePersona?.id ?: 1L,
                            onSelectPersona = { viewModel.selectPersona(it) },
                            onCreateCustomPersona = { name, role, desc, prompt, avatar, temp ->
                                viewModel.createCustomPersona(name, role, desc, prompt, avatar, temp)
                            }
                        )
                    }

                    AppDestination.AGENTS -> {
                        AgentDashboardScreen(viewModel = viewModel)
                    }

                    AppDestination.ANALYTICS -> {
                        AnalyticsScreen(
                            totalTokens = totalTokens ?: 0,
                            avgLatency = avgLatency ?: 0.0,
                            offlineCount = offlineCount,
                            onlineCount = onlineCount,
                            recentLogs = recentLogs
                        )
                    }

                    AppDestination.SETTINGS -> {
                        val currentTheme by viewModel.themeMode.collectAsState()
                        SettingsScreen(
                            currentTheme = currentTheme,
                            onThemeChange = { viewModel.setThemeMode(it) },
                            onClearAllData = { viewModel.clearAllData() }
                        )
                    }
                }
            }
        }
    }

    // ── Model Selector Dialog ────────────────────────────────────────────
    if (showModelDialog) {
        ModelSelectorDialog(
            currentModel = selectedModel,
            currentMode = modePreference,
            hubModels = hubModels,
            onModelSelected = { newModel, newMode ->
                viewModel.setModelAndMode(newModel, newMode)
            },
            onDownloadModel = { viewModel.startModelDownload(it) },
            onCancelDownload = { viewModel.cancelModelDownload(it) },
            onDeleteModel = { viewModel.deleteHubModel(it) },
            onDismiss = { showModelDialog = false }
        )
    }

    // ── Security Badge Dialog ────────────────────────────────────────────
    if (showSecurityDialog) {
        SecurityBadgeDialog(
            sampleText = "Sesi Nusantara AI terenkripsi penuh dengan AES-256-GCM hardware Android Keystore.",
            onDismiss = { showSecurityDialog = false }
        )
    }

    // ── Phase 3: Diagnostics & Telemetry Dialog ──────────────────────────
    if (showDiagnosticsDialog) {
        DiagnosticsDialog(
            telemetry = telemetrySnapshot,
            onDismiss = { showDiagnosticsDialog = false }
        )
    }

    // ── Phase 5: Mesh Intelligence Network Dialog ────────────────────────
    if (showMeshDialog) {
        com.example.ui.components.MeshIntelligenceDialog(
            meshState = meshState,
            onRefreshDiscovery = { viewModel.refreshMeshDiscovery() },
            onToggleMesh = { viewModel.toggleMeshNetwork(it) },
            onDismiss = { showMeshDialog = false }
        )
    }

    // ── Phase 4: Enterprise RAG Compliance Dialog ────────────────────────
    if (showEnterpriseDialog) {
        com.example.ui.components.EnterpriseRAGDialog(
            ragStats = ragStats,
            searchResults = ragSearchResults,
            swarmState = swarmWorkflowState,
            roiSummary = enterpriseROISummary,
            gatewayState = enterpriseGatewayState,
            templates = emptyList(),
            onSearchQuery = { viewModel.searchVectorRAG(it) },
            onIngestSampleDoc = { viewModel.ingestSampleEnterpriseDocument() },
            onStartSwarmWorkflow = { viewModel.startSwarmWorkflow(it) },
            onDismiss = { showEnterpriseDialog = false }
        )
    }

    // ── Phase 5: Sovereign AGI Swarm Dialog ──────────────────────────────
    if (showSovereignAGIDialog) {
        com.example.ui.components.SovereignAGIDialog(
            onDismiss = { showSovereignAGIDialog = false }
        )
    }
}
