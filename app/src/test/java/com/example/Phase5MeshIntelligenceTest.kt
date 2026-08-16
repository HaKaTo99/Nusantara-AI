package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.mesh.ComputeTier
import com.example.domain.mesh.P2PMeshIntelligenceManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Phase5MeshIntelligenceTest {

    private lateinit var context: Context
    private lateinit var meshManager: P2PMeshIntelligenceManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        meshManager = P2PMeshIntelligenceManager(context)
    }

    @Test
    fun testMeshDiscovery_InitialStateAndPeers() = runBlocking {
        // Allow simulated discovery
        kotlinx.coroutines.delay(1500)

        val state = meshManager.meshState.value
        assertTrue(state.isMeshActive)
        assertNotNull(state.myNodeId)
        assertTrue(state.connectedPeers.isNotEmpty())

        val flagshipPeer = state.connectedPeers.find { it.computeTier == ComputeTier.FLAGSHIP_NPU }
        assertNotNull(flagshipPeer)
        assertTrue(flagshipPeer?.activeModel?.contains("Garuda AI") == true)
        assertTrue(state.totalDistributedTops >= 40.0)
    }

    @Test
    fun testMeshCooperativeInference_Execution() = runBlocking {
        kotlinx.coroutines.delay(1500)

        val response = meshManager.requestCooperativeInference(
            prompt = "Analisis hukum perjanjian kerja sama desa",
            targetModel = "Garuda AI 3.2B"
        )

        assertNotNull(response)
        assertTrue(response.contains("Kecerdasan Mesh P2P"))
        assertTrue(meshManager.meshState.value.totalKnowledgeBlocksShared > 0)
    }

    @Test
    fun testMeshKnowledgeSync_GossipProtocol() {
        val initialCount = meshManager.meshState.value.totalKnowledgeBlocksShared
        meshManager.syncKnowledgeBlock(
            blockTitle = "Petunjuk Budidaya Padi Organik",
            category = "Pertanian Nusantara"
        )

        val updatedCount = meshManager.meshState.value.totalKnowledgeBlocksShared
        assertEquals(initialCount + 1, updatedCount)
    }

    @Test
    fun testMeshNetwork_ToggleDisableAndEnable() = runBlocking {
        meshManager.toggleMeshNetwork(true)
        assertTrue(meshManager.meshState.value.isMeshActive)
    }

    @Test
    fun testMilitaryGradeSecurity_SealAndVerifyPacket() {
        val guard = meshManager.securityGuard
        val payload = "Dokumen Rahasia Kedaulatan Negara RI #991"
        val sealedPacket = guard.sealAndSignIntelligencePacket(
            senderNodeId = "node-alpha-1",
            targetNodeId = "node-bravo-2",
            rawIntelligencePayload = payload
        )

        assertNotNull(sealedPacket.payloadCipherBase64)
        assertNotNull(sealedPacket.hmacSignatureHex)
        assertTrue(sealedPacket.isHardwareSigned)

        // Verify & Decrypt valid packet
        val result = guard.verifyAndDecryptPacket(sealedPacket)
        assertTrue(result.isSuccess)
        assertEquals(payload, result.getOrNull())
    }

    @Test
    fun testMilitaryGradeSecurity_TamperingDetectionAndHackerBan() {
        val guard = meshManager.securityGuard
        val payload = "Instruksi Pemrosesan Vektor #102"
        val sealedPacket = guard.sealAndSignIntelligencePacket(
            senderNodeId = "hacker-node-99",
            targetNodeId = "node-korban-1",
            rawIntelligencePayload = payload
        )

        // Simulasikan hacker memanipulasi 1 byte cipher
        val tamperedPacket = sealedPacket.copy(
            payloadCipherBase64 = sealedPacket.payloadCipherBase64 + "TAMPERED"
        )

        val result = guard.verifyAndDecryptPacket(tamperedPacket)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("TAMPERING") == true)

        val auditReport = guard.getSecurityAuditReport()
        assertTrue(auditReport.blockedPoisoningAttacks > 0)
        assertTrue(auditReport.bannedHackerNodes.contains("hacker-node-99"))
    }
}
