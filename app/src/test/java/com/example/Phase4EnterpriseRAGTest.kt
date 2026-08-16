package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.dao.DocumentDao
import com.example.data.local.entity.DocumentEntity
import com.example.domain.agent.SwarmAgentOrchestrator
import com.example.domain.agent.TaskStatus
import com.example.domain.crypto.EncryptionManager
import com.example.domain.enterprise.EnterpriseGatewayManager
import com.example.domain.enterprise.NationalEnterpriseConnector
import com.example.domain.rag.LocalVectorRAGEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * =====================================================================
 * NUSANTARA AI - PHASE 4 AUTOMATED UNIT TESTS: ENTERPRISE & VECTOR RAG
 * Lead System Architect: Herman Krisnanto
 *
 * Verifies:
 * 1. Local Vector RAG Cosine Similarity & Smart Chunking
 * 2. Multi-Agent DAG Task Decomposition & Swarm Execution
 * 3. Indonesian GovTech e-Faktur Pajak 16-Digit NIK & PPN 11% Compliance
 * 4. Data Exfiltration Blocker (Confidential Internal Policy)
 * 5. Private Sovereign Cluster & Zero-Knowledge ECDH Device Pairing
 * =====================================================================
 */

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class Phase4EnterpriseRAGTest {

    private lateinit var context: Context
    private lateinit var encryptionManager: EncryptionManager
    private lateinit var mockDocumentDao: DocumentDao
    private lateinit var ragEngine: LocalVectorRAGEngine
    private lateinit var swarmOrchestrator: SwarmAgentOrchestrator
    private lateinit var enterpriseConnector: NationalEnterpriseConnector
    private lateinit var gatewayManager: EnterpriseGatewayManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        encryptionManager = EncryptionManager.getInstance(context)

        mockDocumentDao = object : DocumentDao {
            val docs = mutableListOf<DocumentEntity>()
            override fun getAllDocuments(): Flow<List<DocumentEntity>> = flowOf(docs)
            override suspend fun getDocumentById(id: Long): DocumentEntity? = docs.find { it.id == id }
            override suspend fun insertDocument(document: DocumentEntity): Long {
                val newId = (docs.size + 1).toLong()
                docs.add(document.copy(id = newId))
                return newId
            }
            override suspend fun deleteDocument(id: Long) {
                docs.removeAll { it.id == id }
            }
        }

        ragEngine = LocalVectorRAGEngine(context, mockDocumentDao, encryptionManager)
        swarmOrchestrator = SwarmAgentOrchestrator(context)
        enterpriseConnector = NationalEnterpriseConnector(context)
        gatewayManager = EnterpriseGatewayManager(context)
    }

    @Test
    fun testVectorRAG_CosineSimilarityAndEmbedding() {
        val textA = "kedaulatan data kecerdasan buatan indonesia herman krisnanto"
        val textB = "kedaulatan data dan teknologi ai indonesia"
        val textC = "resep memasak rendang daging sapi padang bumbu rempah"

        val embA = ragEngine.generatePseudoEmbedding(textA, 64)
        val embB = ragEngine.generatePseudoEmbedding(textB, 64)
        val embC = ragEngine.generatePseudoEmbedding(textC, 64)

        assertEquals(64, embA.size)
        assertEquals(64, embB.size)
        assertEquals(64, embC.size)

        val simRelated = ragEngine.cosineSimilarity(embA, embB)
        val simUnrelated = ragEngine.cosineSimilarity(embA, embC)

        assertTrue("Kemiripan dokumen relevan ($simRelated) harus lebih tinggi dari yang tidak relevan ($simUnrelated)", simRelated > simUnrelated)
    }

    @Test
    fun testVectorRAG_SmartChunkingAndIngestion() = runTest {
        val longContent = (1..100).joinToString(" ") { "kata-$it" }
        val chunks = ragEngine.chunkText(longContent, chunkSize = 25, overlap = 5)

        assertTrue("Teks 100 kata harus dipecah menjadi beberapa chunk", chunks.size >= 4)

        val docId = ragEngine.ingestDocument(
            title = "Panduan UU PDP",
            content = "Undang-Undang Pelindungan Data Pribadi menjamin kedaulatan data nasional.",
            fileType = "PDF"
        )
        assertTrue("Document ID harus valid", docId > 0)

        val searchResults = ragEngine.searchHybrid("kedaulatan data", topK = 3)
        assertFalse("Hasil pencarian tidak boleh kosong setelah ingest", searchResults.isEmpty())
        assertEquals("Panduan UU PDP", searchResults.first().chunk.documentTitle)
    }

    @Test
    fun testSwarmAgentOrchestrator_DAGTaskDecomposition() {
        val goal = "Implementasi Arsitektur AI Berdaulat di IKN"
        val tasks = swarmOrchestrator.planDAGTasks(goal)

        assertEquals(4, tasks.size)
        assertTrue(tasks[0].assignedAgentRole.contains("Herman Krisnanto"))
        assertTrue(tasks[1].assignedAgentRole.contains("Hukum"))
        assertTrue(tasks[2].assignedAgentRole.contains("Finansial"))
        assertTrue(tasks[3].assignedAgentRole.contains("Enterprise"))
    }

    @Test
    fun testSwarmAgentOrchestrator_ExecuteSwarmWorkflow() = runTest {
        val goal = "Kajian Kelayakan AI BUMN"
        val result = swarmOrchestrator.executeSwarmWorkflow(goal)

        assertEquals("COMPLETED", result.currentStage)
        assertFalse(result.isRunning)
        assertEquals(1.0f, result.progressPercent, 0.01f)
        assertTrue("Semua sub-task harus berstatus COMPLETED", result.tasks.all { it.status == TaskStatus.COMPLETED })
        assertTrue("Laporan sintesis harus mencantumkan Herman Krisnanto", result.finalSynthesis.contains("Herman Krisnanto"))
    }

    @Test
    fun testNationalEnterpriseConnector_EFakturValidationAndPPN() {
        // Valid 16-digit NIK/NPWP
        val validNik = "3171012345670001"
        val dpp = 10000000.0 // Rp 10.000.000
        val validResult = enterpriseConnector.validateEFaktur(validNik, dpp)

        assertTrue("NIK 16-digit harus valid", validResult.isValid)
        assertEquals(1100000.0, validResult.ppn11Percent, 0.01) // PPN 11% = 1.100.000
        assertEquals(11100000.0, validResult.totalInvoice, 0.01)
        assertTrue(validResult.psakComplianceStatus.contains("COMPLIANT"))

        // Invalid NIK (< 16 digits)
        val invalidNik = "12345"
        val invalidResult = enterpriseConnector.validateEFaktur(invalidNik, dpp)
        assertFalse("NIK < 16 digit harus ditolak", invalidResult.isValid)
        assertTrue(invalidResult.psakComplianceStatus.contains("NON-COMPLIANT"))
    }

    @Test
    fun testNationalEnterpriseConnector_DataExfiltrationBlocker() {
        val confidentialContent = "DOKUMEN CONFIDENTIAL_INTERNAL RAHASIA NEGARA"
        val normalContent = "Informasi publik umum"

        val blockCloud = enterpriseConnector.checkDataExfiltrationPolicy(confidentialContent, "EXTERNAL_CLOUD_SERVER")
        val allowLocal = enterpriseConnector.checkDataExfiltrationPolicy(confidentialContent, "LOCAL_NPU_ENGINE")
        val allowNormal = enterpriseConnector.checkDataExfiltrationPolicy(normalContent, "EXTERNAL_CLOUD_SERVER")

        assertFalse("Dokumen rahasia dilarang dikirim ke external cloud", blockCloud)
        assertTrue("Dokumen rahasia diizinkan diproses di NPU lokal", allowLocal)
        assertTrue("Dokumen normal diizinkan", allowNormal)
    }

    @Test
    fun testEnterpriseGatewayManager_PrivateClusterAndDevicePairing() {
        val initialCluster = gatewayManager.gatewayState.value.activeCluster
        assertEquals("IKN-SOVEREIGN-CLUSTER-01", initialCluster.clusterId)
        assertTrue(initialCluster.tlsVersion.contains("TLS 1.3"))

        val pairedNode = gatewayManager.pairNewDevice(
            deviceName = "Tablet Eksekutif Direksi",
            deviceType = "TABLET"
        )
        assertNotNull(pairedNode)
        assertTrue("Hash ECDH harus ter-generate", pairedNode.ecdhPublicKeyHash.contains("ECDH-SHA256"))
        assertEquals(3, gatewayManager.gatewayState.value.pairedDevices.size)
    }
}
