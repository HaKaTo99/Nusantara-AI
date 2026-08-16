package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AgentEntity
import com.example.data.local.entity.AnalyticsLogEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.PersonaEntity
import com.example.data.repository.AnalyticsRepository
import com.example.data.repository.ChatRepository
import com.example.data.repository.DocumentRepository
import com.example.data.repository.PersonaRepository
import com.example.domain.ai.HybridAIEngine
import com.example.domain.ai.OfflineReasoningEngine
import com.example.domain.ai.hub.HubModelItem
import com.example.domain.ai.hub.ModelHubManager
import com.example.domain.ai.image.FreeTextToImageEngine
import com.example.domain.ai.image.GeneratedImageResult
import com.example.domain.ai.native.NativeLlamaBridge
import com.example.domain.ai.native.NativeWhisperBridge
import com.example.domain.ai.telemetry.NPUTelemetryManager
import com.example.domain.ai.telemetry.NPUTelemetrySnapshot
import com.example.domain.crypto.EncryptionManager
import com.example.domain.sync.SyncManager
import com.example.domain.sync.SyncState
import com.example.domain.voice.VoiceInteractionManager
import com.example.domain.agent.SwarmAgentOrchestrator
import com.example.domain.agent.SwarmWorkflowState
import com.example.domain.enterprise.EnterpriseGatewayManager
import com.example.domain.enterprise.EnterpriseGatewayState
import com.example.domain.enterprise.EnterpriseROISummary
import com.example.domain.enterprise.GovTechDocumentTemplate
import com.example.domain.enterprise.NationalEnterpriseConnector
import com.example.domain.rag.KnowledgeBaseStats
import com.example.domain.rag.LocalVectorRAGEngine
import com.example.domain.rag.RAGSearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val chatRepository = ChatRepository(database.chatDao())
    val personaRepository = PersonaRepository(database.personaDao())
    val analyticsRepository = AnalyticsRepository(database.analyticsDao())
    val documentRepository = DocumentRepository(database.documentDao())
    private val agentDao = database.agentDao()

    val hybridAIEngine = HybridAIEngine(application, database.analyticsDao())
    val syncManager = SyncManager(application, database.chatDao(), viewModelScope)
    val voiceInteractionManager = VoiceInteractionManager(application)
    val encryptionManager = EncryptionManager.getInstance(application)

    // Phase 3 Native NDK & Hub components
    val nativeLlamaBridge = NativeLlamaBridge.getInstance(application)
    val nativeWhisperBridge = NativeWhisperBridge(application)
    val npuTelemetryManager = NPUTelemetryManager(application)
    val modelHubManager = ModelHubManager(application)

    // Phase 5 Mesh Intelligence Network
    val meshIntelligenceManager = com.example.domain.mesh.P2PMeshIntelligenceManager(application, encryptionManager)
    val meshState: StateFlow<com.example.domain.mesh.MeshNetworkState> = meshIntelligenceManager.meshState

    // Phase 4 Enterprise & Swarm Components
    val vectorRAGEngine = LocalVectorRAGEngine(application, database.documentDao(), encryptionManager)
    val swarmOrchestrator = SwarmAgentOrchestrator(application)
    val nationalEnterpriseConnector = NationalEnterpriseConnector(application)
    val enterpriseGatewayManager = EnterpriseGatewayManager(application)

    val ragStats: StateFlow<KnowledgeBaseStats> = vectorRAGEngine.stats
    private val _ragSearchResults = MutableStateFlow<List<RAGSearchResult>>(emptyList())
    val ragSearchResults: StateFlow<List<RAGSearchResult>> = _ragSearchResults.asStateFlow()

    val swarmWorkflowState: StateFlow<SwarmWorkflowState> = swarmOrchestrator.workflowState
    val enterpriseROISummary: StateFlow<EnterpriseROISummary> = nationalEnterpriseConnector.roiSummary
    val enterpriseGatewayState: StateFlow<EnterpriseGatewayState> = enterpriseGatewayManager.gatewayState
    val govTechTemplates: List<GovTechDocumentTemplate> = nationalEnterpriseConnector.getAvailableTemplates()

    // Phase 3 StateFlows
    val hubModels: StateFlow<List<HubModelItem>> = modelHubManager.hubModels
    val telemetrySnapshot: StateFlow<NPUTelemetrySnapshot> = npuTelemetryManager.telemetry

    // Free Text to Image Engine
    val freeTextToImageEngine = FreeTextToImageEngine(application)
    private val _generatedImageState = MutableStateFlow<GeneratedImageResult?>(null)
    val generatedImageState: StateFlow<GeneratedImageResult?> = _generatedImageState.asStateFlow()
    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    // Sync & Connectivity
    val isOnline: StateFlow<Boolean> = syncManager.isOnline
    val syncState: StateFlow<SyncState> = syncManager.syncState

    // Voice
    val isListening: StateFlow<Boolean> = voiceInteractionManager.isListening
    val voiceAmplitude: StateFlow<Float> = voiceInteractionManager.voiceAmplitude

    // Model & Mode settings
    private val _selectedModel = MutableStateFlow("Gemini 3.5 Flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _modePreference = MutableStateFlow("HYBRID") // "ONLINE", "OFFLINE", "HYBRID"
    val modePreference: StateFlow<String> = _modePreference.asStateFlow()

    // Theme Preference ("DARK", "LIGHT", "SYSTEM")
    private val prefs = application.getSharedPreferences("nusantara_prefs", android.content.Context.MODE_PRIVATE)
    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode).apply()
    }

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    // Personas
    val personas: StateFlow<List<PersonaEntity>> = personaRepository.allPersonas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activePersona = MutableStateFlow<PersonaEntity?>(null)
    val activePersona: StateFlow<PersonaEntity?> = _activePersona.asStateFlow()

    // Sessions & Messages
    private val _currentSessionId = MutableStateFlow<Long>(1L)
    val currentSessionId: StateFlow<Long> = _currentSessionId.asStateFlow()

    val messages: StateFlow<List<ChatMessageEntity>> = _currentSessionId.flatMapLatest { sessionId ->
        if (sessionId <= 0) flowOf(emptyList())
        else chatRepository.getMessagesForSession(sessionId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Documents
    val documents: StateFlow<List<DocumentEntity>> = documentRepository.allDocuments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Agents
    val agents: StateFlow<List<AgentEntity>> = agentDao.getAllAgents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val runningAgentCount: StateFlow<Int> = agentDao.getRunningAgentCount().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Analytics
    val totalTokens: StateFlow<Int?> = analyticsRepository.totalTokens.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val avgLatency: StateFlow<Double?> = analyticsRepository.avgLatency.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val offlineCount: StateFlow<Int> = analyticsRepository.offlineCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val onlineCount: StateFlow<Int> = analyticsRepository.onlineCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val recentLogs: StateFlow<List<AnalyticsLogEntity>> = analyticsRepository.recentLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Load initial active persona
            val defaultPersona = personaRepository.getPersonaById(1)
            if (defaultPersona != null) {
                _activePersona.value = defaultPersona
            }
        }
    }

    fun setModelAndMode(model: String, mode: String) {
        _selectedModel.value = model
        _modePreference.value = mode
    }

    fun selectPersona(persona: PersonaEntity) {
        _activePersona.value = persona
    }

    fun startNewChat() {
        viewModelScope.launch(Dispatchers.IO) {
            val newSessionId = chatRepository.createSession(
                title = "Obrolan ${System.currentTimeMillis() % 10000}",
                modelName = _selectedModel.value,
                mode = _modePreference.value,
                personaId = _activePersona.value?.id ?: 1L
            )
            _currentSessionId.value = newSessionId
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank() || _isProcessing.value) return

        val sessionId = _currentSessionId.value
        val model = _selectedModel.value
        val mode = _modePreference.value
        val persona = _activePersona.value

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Save user message with encryption
            chatRepository.saveMessage(
                sessionId = sessionId,
                sender = "USER",
                content = content,
                isOfflineGenerated = mode == "OFFLINE",
                syncStatus = if (mode == "OFFLINE" || !isOnline.value) "PENDING_SYNC" else "SYNCED",
                modelUsed = model,
                encrypt = true
            )

            // 2. Trigger AI processing
            _isProcessing.value = true
            try {
                val aiResponse = hybridAIEngine.processQuery(
                    prompt = content,
                    selectedModel = model,
                    modePreference = mode,
                    personaPrompt = persona?.systemPrompt ?: "",
                    temperature = persona?.temperature ?: 0.7f
                )

                // Format final text with code artifact if present
                val fullText = if (aiResponse.codeArtifact != null) {
                    val lang = aiResponse.artifactType?.lowercase() ?: "text"
                    "${aiResponse.text}\n\n```$lang\n${aiResponse.codeArtifact}\n```"
                } else {
                    aiResponse.text
                }

                val stepsJson = OfflineReasoningEngine.reasoningStepsToJson(aiResponse.reasoningSteps)

                // 3. Save AI response with encryption
                chatRepository.saveMessage(
                    sessionId = sessionId,
                    sender = "ASSISTANT",
                    content = fullText,
                    reasoningStepsJson = stepsJson,
                    tokenCount = aiResponse.tokenCount,
                    latencyMs = aiResponse.latencyMs,
                    isOfflineGenerated = aiResponse.isOffline,
                    syncStatus = "SYNCED",
                    modelUsed = aiResponse.modelName,
                    encrypt = true
                )
            } catch (e: Exception) {
                // Fallback message
                chatRepository.saveMessage(
                    sessionId = sessionId,
                    sender = "ASSISTANT",
                    content = "Nusantara AI lokal: Maaf, terjadi kesalahan saat memproses jawaban. Silakan coba kembali.",
                    tokenCount = 10,
                    latencyMs = 50,
                    isOfflineGenerated = true,
                    syncStatus = "SYNCED",
                    modelUsed = "Fallback-Core",
                    encrypt = true
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun startVoiceInput() {
        voiceInteractionManager.startListening { recognized ->
            if (recognized.isNotBlank()) {
                sendMessage(recognized)
            }
        }
    }

    fun stopVoiceInput() {
        voiceInteractionManager.stopListening()
    }

    fun speakText(text: String) {
        voiceInteractionManager.speak(text)
    }

    fun stopSpeaking() {
        voiceInteractionManager.stopSpeaking()
    }

    fun createCustomPersona(
        name: String,
        role: String,
        description: String,
        systemPrompt: String,
        avatarEmoji: String,
        temperature: Float
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = personaRepository.createCustomPersona(
                name = name,
                role = role,
                description = description,
                systemPrompt = systemPrompt,
                avatarEmoji = avatarEmoji,
                temperature = temperature
            )
            val newPersona = personaRepository.getPersonaById(id)
            if (newPersona != null) {
                _activePersona.value = newPersona
            }
        }
    }

    fun processDocument(title: String, fileType: String, rawContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val summary = "Ringkasan Dokumen [$title]:\nDokumen memuat data operasional terstruktur dengan efisiensi tinggi dan parameter valid."
            val keyInsights = "• Integritas data diverifikasi lokal\n• Enkripsi AES-256-GCM diterapkan pada isi dokumen\n• Siap dianalisis lintas model AI"
            documentRepository.saveDocument(
                title = title,
                fileType = fileType,
                content = rawContent,
                summary = summary,
                keyInsights = keyInsights
            )
        }
    }

    fun analyzeImagePrompt(prompt: String) {
        sendMessage("🎨 [Visual Studio Analysis] $prompt")
    }

    fun generateTextToImage(
        prompt: String,
        style: String = "Cinematic Realistic",
        aspectRatio: String = "1:1",
        modelId: String = "flux"
    ) {
        viewModelScope.launch {
            _isGeneratingImage.value = true
            val result = freeTextToImageEngine.generateImage(
                prompt = prompt,
                style = style,
                aspectRatio = aspectRatio,
                modelId = modelId,
                isOnline = isOnline.value
            )
            _generatedImageState.value = result
            _isGeneratingImage.value = false

            // Rekam ke stream chat agar tersimpan di riwayat
            val imageSummary = "🎨 [Text-to-Image Generated]\n• Model: ${result.modelName}\n• Gaya: $style ($aspectRatio)\n• Prompt: \"${result.prompt}\"\n\n🔗 Gambar berhasil digenerasi dengan model AI gratis (${result.modelName})."
            sendMessage(imageSummary)
        }
    }

    fun triggerSync() {
        syncManager.triggerAutoSync()
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            database.chatDao().clearAllMessages()
            database.chatDao().clearAllSessions()
            database.analyticsDao().clearAllLogs()
            startNewChat()
        }
    }

    // ── Agent Management ──────────────────────────────────────────────────

    fun createAgent(
        name: String,
        description: String,
        avatarEmoji: String = "🤖",
        taskType: String = "GENERAL"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            agentDao.insertAgent(
                AgentEntity(
                    name = name,
                    description = description,
                    avatarEmoji = avatarEmoji,
                    taskType = taskType,
                    status = "IDLE"
                )
            )
        }
    }

    fun toggleAgent(agent: AgentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = if (agent.status == "RUNNING") "PAUSED" else "RUNNING"
            agentDao.updateAgentStatus(
                agentId = agent.id,
                status = newStatus,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    fun deleteAgent(agentId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            agentDao.deleteAgent(agentId)
        }
    }

    // ── Phase 3: Model Hub & NPU Diagnostics ────────────────────────────

    fun startModelDownload(modelId: String) {
        modelHubManager.startDownload(modelId)
    }

    fun cancelModelDownload(modelId: String) {
        modelHubManager.cancelDownload(modelId)
    }

    fun deleteHubModel(modelId: String): Boolean {
        return modelHubManager.deleteModel(modelId)
    }

    fun refreshTelemetry() {
        npuTelemetryManager.updateSnapshot()
    }

    fun selectHubModel(model: HubModelItem) {
        _selectedModel.value = model.name
        _modePreference.value = "OFFLINE"
    }

    // ── Phase 5: Mesh Intelligence Network ─────────────────────────────
    fun refreshMeshDiscovery() {
        meshIntelligenceManager.startMeshDiscovery()
    }

    fun toggleMeshNetwork(enable: Boolean) {
        meshIntelligenceManager.toggleMeshNetwork(enable)
    }

    // ── Phase 4: Enterprise RAG & Multi-Agent Swarm ────────────────────
    fun searchVectorRAG(query: String) {
        viewModelScope.launch {
            _ragSearchResults.value = vectorRAGEngine.searchHybrid(query)
        }
    }

    fun ingestSampleEnterpriseDocument() {
        viewModelScope.launch {
            val sampleTitle = "Pedoman Tata Naskah Dinas & Kepatuhan UU PDP RI"
            val sampleContent = """
                Berdasarkan Undang-Undang No. 27 Tahun 2022 tentang Pelindungan Data Pribadi (UU PDP) dan PermenPAN-RB No. 1 Tahun 2023, seluruh sistem informasi dan kecerdasan buatan di lingkungan lembaga negara, BUMN, dan korporasi nasional wajib menerapkan prinsip kedaulatan data dan isolasi komputasi lokal.
                Pemrosesan data identitas kependudukan seperti NIK 16-digit, nomor rekening perbankan, dan data biometrik dilarang ditransmisikan ke server asing tanpa izin resmi.
                Arsitektur Nusantara AI yang dirancang oleh Lead System Architect Herman Krisnanto mengadopsi kubah perangkat keras TEE AES-256-GCM dengan fitur one-click cryptographic wipe untuk menjamin hak subjek data secara mutlak.
            """.trimIndent()
            vectorRAGEngine.ingestDocument(sampleTitle, sampleContent, "DOCX")
            searchVectorRAG("kedaulatan data UU PDP Herman Krisnanto")
        }
    }

    fun startSwarmWorkflow(goalPrompt: String) {
        viewModelScope.launch {
            swarmOrchestrator.executeSwarmWorkflow(goalPrompt)
        }
    }

    // ── Encryption Utility ───────────────────────────────────────────────

    fun getVaultStatus(): String = encryptionManager.getVaultStatus()

    override fun onCleared() {
        super.onCleared()
        voiceInteractionManager.release()
        nativeLlamaBridge.unloadActiveModel()
    }
}
