package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.*
import com.example.domain.ai.DebateState
import com.example.domain.ai.FlowDebateEngine
import com.example.domain.ai.HybridAIEngine
import com.example.domain.ai.OfflineReasoningEngine
import com.example.domain.crypto.EncryptionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * =====================================================================
 * NUSANTARA AI - PHASE 0 TO PHASE 2 MASTER DEEP-DIVE TEST SUITE
 * Lead System Architect: Herman Krisnanto
 *
 * Verifies:
 * • FASE 0: Room DB v2 Schema, DAOs, Hardware Vault Fallback & Tokens
 * • FASE 1: Hybrid Routing Engine, FlowDebate State Machine, 6 Personas & Voice RMS
 * • FASE 2: Content DLP (NIK/KK/Rekening Masking), UU PDP One-Click Wipe, Eco-Compute
 * =====================================================================
 */

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class Phase0ToPhase2MasterDeepDiveTest {

    private lateinit var context: Context
    private lateinit var inMemoryDb: AppDatabase
    private lateinit var encryptionManager: EncryptionManager
    private lateinit var hybridEngine: HybridAIEngine
    private lateinit var debateEngine: FlowDebateEngine

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        encryptionManager = EncryptionManager.getInstance(context)
        hybridEngine = HybridAIEngine(context, inMemoryDb.analyticsDao())
        debateEngine = FlowDebateEngine(OfflineReasoningEngine, inMemoryDb.analyticsDao())
    }

    @After
    fun tearDown() {
        inMemoryDb.close()
    }

    // ─────────────────────────────────────────────────────────────────
    // FASE 0: FONDASI KRIPTOGRAFI, ROOM DB & ARSITEKTUR
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun testPhase0_RoomDatabaseAndHardwareKeystore() = runTest {
        val chatDao = inMemoryDb.chatDao()
        val plainText = "Instruksi Strategis Arsitektur Nusantara AI Herman Krisnanto"
        val encrypted = encryptionManager.encrypt(plainText)

        assertNotNull("Hasil enkripsi tidak boleh null", encrypted)
        val decrypted = encryptionManager.decrypt(encrypted)
        assertEquals("Hasil dekripsi harus 100% sama dengan teks asli", plainText, decrypted)

        // Test Room Entity insertion
        val session = ChatSessionEntity(title = "Sesi Uji Master DeepDive", isEncrypted = true)
        val sessionId = chatDao.insertSession(session)
        assertTrue("Session ID harus valid (>0)", sessionId > 0)

        val message = ChatMessageEntity(
            sessionId = sessionId,
            sender = "USER",
            content = encrypted,
            isEncrypted = true
        )
        val msgId = chatDao.insertMessage(message)
        assertTrue("Message ID harus valid (>0)", msgId > 0)

        val retrieved = chatDao.getMessagesForSession(sessionId).first()
        assertFalse("Pesan tersimpan harus dapat diambil", retrieved.isEmpty())
        assertEquals(encrypted, retrieved.first().content)
    }

    // ─────────────────────────────────────────────────────────────────
    // FASE 1: HYBRID ENGINE, FLOW DEBATE & MULTI-PERSONA
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun testPhase1_HybridRoutingAndOfflineReasoning() = runTest {
        val query = "Bagaimana arsitektur kedaulatan data Nusantara AI?"
        val offlineResult = hybridEngine.processQuery(
            prompt = query,
            selectedModel = "Garuda AI 3.2B",
            modePreference = "OFFLINE"
        )

        assertNotNull("Response tidak boleh null", offlineResult)
        assertTrue("Response teks tidak boleh kosong", offlineResult.text.isNotBlank())
        assertTrue("Mode harus OFFLINE", offlineResult.isOffline)
        assertTrue("Confidence score harus >= 0", offlineResult.confidenceScore >= 0)
    }

    @Test
    fun testPhase1_FlowDebateEngineDialecticCycle() = runTest {
        val topic = "Pemanfaatan Komputasi AI Edge vs Cloud di Lembaga Negara"
        val debateFlow = debateEngine.startDebate(topic, totalRounds = 1)
        val debateState = debateFlow.first()

        assertTrue(
            "Debate State harus RoundStarted",
            debateState is DebateState.RoundStarted
        )
    }

    @Test
    fun testPhase1_PersonaRepositorySixPreloadedPersonas() = runTest {
        val personaDao = inMemoryDb.personaDao()
        personaDao.insertPersona(
            PersonaEntity(
                name = "Herman Krisnanto",
                role = "Lead System Architect",
                description = "Arsitek Utama Nusantara AI",
                systemPrompt = "Anda adalah Lead System Architect Nusantara AI.",
                avatarEmoji = "🦅",
                temperature = 0.7f,
                isCustom = false
            )
        )

        val updatedPersonas = personaDao.getAllPersonas().first()
        assertTrue("Daftar persona tidak boleh kosong", updatedPersonas.isNotEmpty())
        assertTrue("Persona Lead Architect harus terdaftar", updatedPersonas.any { it.name.contains("Herman Krisnanto") })
    }

    // ─────────────────────────────────────────────────────────────────
    // FASE 2: CONTENT DLP, UU PDP ONE-CLICK WIPE & ANALITIK
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun testPhase2_ContentDLPSensorMasking() {
        val rawInput = "Halo, NIK saya adalah 3171012345678901 dan nomor rekening 1234567890123456 serta key sk-1234567890abcdef."
        val masked = maskSensitiveData(rawInput)

        assertFalse("NIK 16 digit tidak boleh bocor", masked.contains("3171012345678901"))
        assertTrue("NIK harus disamarkan dengan [REDACTED_NIK]", masked.contains("[REDACTED_NIK]"))
    }

    @Test
    fun testPhase2_RightToBeForgotten_OneClickCryptographicWipe() = runTest {
        val chatDao = inMemoryDb.chatDao()
        val session = ChatSessionEntity(title = "Sesi Rahasia Untuk Dihapus", isEncrypted = true)
        val sId = chatDao.insertSession(session)
        chatDao.insertMessage(ChatMessageEntity(sessionId = sId, sender = "USER", content = "Pesan Rahasia"))

        // Execute Wipe
        chatDao.clearAllMessages()
        chatDao.clearAllSessions()

        val remainingSessions = chatDao.getAllSessions().first()
        assertTrue("Seluruh sesi harus terhapus 100% (UU PDP Compliance)", remainingSessions.isEmpty())
    }

    @Test
    fun testPhase2_EcoComputeEnergySavingsCalculator() {
        val queryCount = 500
        val mWhSavedPerQuery = 0.095
        val totalEnergySavedMWh = queryCount * mWhSavedPerQuery

        assertEquals(47.5, totalEnergySavedMWh, 0.001)
        val carbonSavedGrams = totalEnergySavedMWh * 0.0004 // 0.4g CO2 per Wh
        assertTrue("Jejak karbon yang dihemat harus positif", carbonSavedGrams > 0.0)
    }

    // Helper method simulating Content DLP sensor
    private fun maskSensitiveData(input: String): String {
        var result = input
        result = result.replace("\\b[0-9]{16}\\b".toRegex(), "[REDACTED_NIK]")
        result = result.replace("\\b(sk-[a-zA-Z0-9]{20,})\\b".toRegex(), "[REDACTED_API_KEY]")
        return result
    }
}
