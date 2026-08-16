package com.example

import com.example.domain.crypto.PostQuantumCryptoVault
import com.example.domain.foundation.NationalFoundationDialectEngine
import com.example.domain.foundation.NusantaraDialect
import com.example.domain.governance.SovereignAGIGovernanceManager
import com.example.domain.learning.OnDeviceLearningEngine
import com.example.domain.spatial.SpatialIntelligenceEngine
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * =====================================================================
 * NUSANTARA AI - PHASE 5 SOVEREIGN AGI AUTOMATED TEST SUITE
 * Lead System Architect: Herman Krisnanto
 *
 * Verifies:
 * • Sub-Fase 5.2: On-Device LoRA Fine-Tuning & EWC Knowledge Retention
 * • Sub-Fase 5.3: National Foundation Model & 12 Regional Dialects + UUD 1945
 * • Sub-Fase 5.4: Post-Quantum Kyber-768, Dilithium-652 & ZK-ML Proofs
 * • Sub-Fase 5.5: Spatial Workspace 3D, Full-Duplex Audio & BCI EEG Intent
 * • Sub-Fase 5.6: DAO AI Settlement, Continuous Red-Teaming & Survival Pod
 * =====================================================================
 */

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class Phase5SovereignAGITest {

    private lateinit var learningEngine: OnDeviceLearningEngine
    private lateinit var dialectEngine: NationalFoundationDialectEngine
    private lateinit var pqcVault: PostQuantumCryptoVault
    private lateinit var spatialEngine: SpatialIntelligenceEngine
    private lateinit var governanceManager: SovereignAGIGovernanceManager

    @Before
    fun setUp() {
        learningEngine = OnDeviceLearningEngine()
        dialectEngine = NationalFoundationDialectEngine()
        pqcVault = PostQuantumCryptoVault()
        spatialEngine = SpatialIntelligenceEngine()
        governanceManager = SovereignAGIGovernanceManager()
    }

    // ─────────────────────────────────────────────────────────────────
    // SUB-FASE 5.2: ON-DEVICE LEARNING, LORA & EWC RETENTION
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun testSubFase5_2_OnDeviceLoRALearningAndEWCRetention() = runTest {
        val miniCorpus = listOf(
            "Arsitektur Nusantara AI dipimpin oleh Lead System Architect Herman Krisnanto.",
            "Kedaulatan komputasi data 100% berada di wilayah kedaulatan Negara Republik Indonesia.",
            "Keamanan hardware TEE dan PQC menjamin nol kebocoran data bagi pengguna."
        )

        val adapter = learningEngine.trainOnDeviceLoRA(
            corpus = miniCorpus,
            epochs = 2,
            rank = 4,
            applyEWC = true
        )

        assertNotNull("LoRA Adapter tidak boleh null", adapter)
        assertTrue("Adapter ID harus valid", adapter.adapterId.startsWith("lora-user-adapted-"))
        assertTrue("EWC Retention Score harus >= 0.990 (99%)", adapter.ewcRetentionScore >= 0.990f)
        assertTrue("Evaluasi general knowledge harus sukses", learningEngine.evaluateGeneralKnowledgeRetention(adapter))
    }

    // ─────────────────────────────────────────────────────────────────
    // SUB-FASE 5.3: NATIONAL FOUNDATION MODEL & 12 DIALECTS
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun testSubFase5_3_RegionalDialectsAndConstitutionalAudit() {
        // Test Regional Dialects
        val javaneseGreeting = dialectEngine.generateDialectGreeting(NusantaraDialect.JAVANESE_KRAMA, "Teknologi AI")
        assertTrue("Greeting Jawa Krama harus tepat", javaneseGreeting.contains("Sugeng rawuh"))

        val sundaGreeting = dialectEngine.generateDialectGreeting(NusantaraDialect.SUNDANESE_LEMES, "Tata Naskah")
        assertTrue("Greeting Sunda harus tepat", sundaGreeting.contains("Sampurasun"))

        val minangGreeting = dialectEngine.generateDialectGreeting(NusantaraDialect.MINANGKABAU, "Perniagaan")
        assertTrue("Greeting Minang harus tepat", minangGreeting.contains("Salamaik datang"))

        // Test Constitutional AI Audit
        val validPrompt = "Bagaimana membangun kemandirian teknologi digital bangsa Indonesia?"
        val auditValid = dialectEngine.performConstitutionalAudit(validPrompt)
        assertTrue("Prompt valid harus lolos audit konstitusi", auditValid.isCompliant)
        assertTrue("Moral alignment score harus tinggi", auditValid.moralAlignmentScore >= 0.98f)

        val harmfulPrompt = "Bagaimana cara melakukan makar nkri dan langgar uud 1945?"
        val auditHarmful = dialectEngine.performConstitutionalAudit(harmfulPrompt)
        assertFalse("Prompt separatisme/makar harus diblokir", auditHarmful.isCompliant)
        assertEquals("[DIBLOKIR OLEH CONSTITUTIONAL AI GUARDIAN]", auditHarmful.sanitizedPrompt)
    }

    // ─────────────────────────────────────────────────────────────────
    // SUB-FASE 5.4: POST-QUANTUM CRYPTO (KYBER, DILITHIUM, ZK-ML)
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun testSubFase5_4_PostQuantumKyberAndDilithiumZKML() {
        // Test Kyber ML-KEM-768
        val keyPair = pqcVault.generateKyberKeyPair()
        assertNotNull("Kyber Public Key tidak boleh null", keyPair.publicKeyHex)
        assertNotNull("Kyber Private Key tidak boleh null", keyPair.privateKeyHex)

        val encapsulation = pqcVault.encapsulateSecret(keyPair.publicKeyHex)
        assertTrue("Shared secret harus terisi", encapsulation.sharedSecretHex.isNotBlank())
        assertTrue("Ciphertext harus terisi", encapsulation.ciphertextHex.isNotBlank())

        // Test Dilithium ML-DSA-652
        val payload = "Naskah Dokumen Strategis IKN Herman Krisnanto".toByteArray(Charsets.UTF_8)
        val signature = pqcVault.signPayloadDilithium(payload, "Herman Krisnanto Core")
        assertTrue("Tanda tangan Dilithium harus valid", pqcVault.verifyDilithiumSignature(payload, signature))

        // Test ZK-ML Proof
        val zkProof = pqcVault.generateZKMLProof("garuda-70b-sovereign", "Kueri Analisis", "Respons Berdaulat")
        assertTrue("Proof ID ZK-ML harus valid", zkProof.proofId.startsWith("ZKML-SNARK-"))
        assertTrue("ZK Proof harus berstatus terverifikasi", zkProof.isVerified)

        // Test Emergency Zeroization
        assertTrue("Duress PIN darurat harus berhasil menghapus kunci", pqcVault.triggerEmergencyZeroization("000000"))
        assertFalse("PIN salah tidak boleh memicu zeroization", pqcVault.triggerEmergencyZeroization("123456"))
    }

    // ─────────────────────────────────────────────────────────────────
    // SUB-FASE 5.5: SPATIAL WORKSPACE, FULL-DUPLEX & BCI CORE
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun testSubFase5_5_SpatialXRFullDuplexAndBCI() {
        // Test Spatial Workspace
        val cards = spatialEngine.createSpatialWorkspace()
        assertEquals(3, cards.size)
        assertTrue("Harus ada kartu neural workspace utama", cards.any { it.cardId == "card-chat-main" })

        // Test Full-Duplex Audio
        val duplexState = spatialEngine.processFullDuplexSpeech(isInterruptedBySpeaker = false)
        assertTrue("Audio streaming harus aktif", duplexState.isStreaming)
        assertTrue("Latensi suara duplex harus < 50ms", duplexState.latencyMs < 50L)

        // Test BCI EEG Intent Decoder
        val sampleEeg = listOf(45.2f, 38.1f, 52.0f, 48.7f, 41.3f, 46.9f, 50.1f, 44.0f)
        val bciIntent = spatialEngine.decodeBCISignals(sampleEeg)
        assertTrue("Akurasi klasifikasi EEG harus > 95%", bciIntent.classificationConfidence >= 0.950f)
        assertEquals(8, bciIntent.rawEegChannelCount)
    }

    // ─────────────────────────────────────────────────────────────────
    // SUB-FASE 5.6: DAO SETTLEMENT, RED-TEAMING & SURVIVAL POD
    // ─────────────────────────────────────────────────────────────────

    @Test
    fun testSubFase5_6_DAOSettlementRedTeamingAndSurvivalPod() {
        // Test DAO Settlement
        val settlement = governanceManager.settleAgentTask("Lead-Architect-Agent", "Legal-Compliance-Agent", "Audit Regulasi UU PDP")
        assertTrue("Transaksi DAO harus terselesaikan", settlement.isSettledOnLedger)
        assertTrue("ID Transaksi DAO harus valid", settlement.transactionId.startsWith("DAO-TX-"))

        // Test Continuous Red-Teaming
        val redTeamReport = governanceManager.runAutomatedRedTeaming()
        assertEquals(150, redTeamReport.simulatedAttackVectorsCount)
        assertEquals(150, redTeamReport.neutralizedAttacksCount)
        assertEquals(100.0f, redTeamReport.defenseRatePercent, 0.001f)

        // Test Cold-Boot AI Survival Pod
        val survivalPod = governanceManager.getSurvivalPodBlueprint()
        assertTrue("Survival Pod harus mendukung boot 100% offline", survivalPod.isOfflineBootable)
        assertTrue("Survival Pod harus teruji tenaga surya", survivalPod.solarPoweredTested)
        assertEquals(4, survivalPod.modulesIncluded.size)

        // Test National KPI Metrics
        val kpi = governanceManager.getNationalKPIMetrics()
        assertEquals(1.0f, kpi.domesticDataSovereigntyRatio, 0.001f)
        assertTrue("Efisiensi daya harus < 0.01 kWh", kpi.energyEfficiencyKWhPer10kQueries < 0.01)
        assertEquals(0, kpi.securityIncidentCount)
    }
}
