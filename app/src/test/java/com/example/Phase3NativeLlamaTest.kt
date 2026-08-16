package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.ai.hub.ModelHubManager
import com.example.domain.ai.native.GGUFMetadataParser
import com.example.domain.ai.native.NativeLlamaBridge
import com.example.domain.ai.native.NativeWhisperBridge
import com.example.domain.ai.telemetry.NPUTelemetryManager
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Phase3NativeLlamaTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testGGUFMetadataParser_SyntheticFile() {
        val testFile = File(context.cacheDir, "qwen2.5-3b-instruct-q4_k_m.gguf")
        testFile.writeBytes("GGUF\u0003\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000".toByteArray())

        val header = GGUFMetadataParser.parseHeader(testFile)
        assertTrue(header.isValidGGUF)
        assertEquals("GGUF", header.magic)
        assertEquals("qwen2.5", header.architecture)
        assertTrue(header.quantizationType.contains("Q4_K_M"))
        testFile.delete()
    }

    @Test
    fun testNativeLlamaBridge_LifecycleAndStreaming() {
        runBlocking {
            val testModel = File(context.cacheDir, "test_model_q4.gguf")
            testModel.writeBytes("GGUF\u0003\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000".toByteArray())

            val bridge = NativeLlamaBridge.getInstance(context)
            val modelCtx = bridge.loadModel(testModel, nThreads = 4, nGpuLayers = 24)

            assertTrue(bridge.isModelLoaded())
            assertNotNull(modelCtx)
            assertEquals(4, modelCtx.nThreads)

            // Stream tokens test
            val tokens = bridge.generateStream("Halo Nusantara AI").toList()
            assertTrue(tokens.isNotEmpty())

            // Complete response test
            val response = bridge.generateComplete("Halo")
            assertTrue(response.text.isNotBlank())
            assertTrue(response.isOffline)
            assertEquals(92, response.confidenceScore)

            bridge.unloadActiveModel()
            assertFalse(bridge.isModelLoaded())
            testModel.delete()
        }
    }

    @Test
    fun testModelHubManager_CatalogAndStateFlow() {
        val hubManager = ModelHubManager(context)
        val catalog = hubManager.hubModels.value

        assertTrue(catalog.size >= 9)
        val qwenItem = catalog.find { it.id == "qwen-2.5-3b-q4" }
        assertNotNull(qwenItem)
        assertEquals("Qwen 2.5 3.2B Instruct", qwenItem?.name)

        val r1Item = catalog.find { it.id == "deepseek-r1-1.5b-q4" }
        assertNotNull(r1Item)
        assertTrue(r1Item?.description?.contains("FlowDebate") == true)

        val visionItem = catalog.find { it.id == "qwen2-vl-2b-q4" }
        assertNotNull(visionItem)

        val garudaItem = catalog.find { it.id == "garuda-ai-3.2b-q4" }
        assertNotNull(garudaItem)
        assertEquals("Garuda AI 3.2B (Sovereign LLM Indonesia)", garudaItem?.name)
        assertTrue(garudaItem?.description?.contains("Garuda AI") == true)

        val sovereignItem = catalog.find { it.id == "nusantara-llama-3b-id" }
        assertNotNull(sovereignItem)
        assertTrue(sovereignItem?.description?.contains("Herman Krisnanto") == true)

        val bgeItem = catalog.find { it.id == "bge-m3-embedding-int8" }
        assertNotNull(bgeItem)

        val piperItem = catalog.find { it.id == "piper-tts-id-gadis" }
        assertNotNull(piperItem)
    }

    @Test
    fun testNPUTelemetryManager_MetricsCalculation() {
        val telemetryManager = NPUTelemetryManager(context)
        telemetryManager.startInferenceSession()
        telemetryManager.recordToken()
        telemetryManager.recordToken()
        telemetryManager.completeInferenceSession(totalTokens = 20, latencyMs = 500, isNpu = true)

        val snapshot = telemetryManager.telemetry.value
        assertTrue(snapshot.tokensPerSecond > 0f)
        assertTrue(snapshot.timeToFirstTokenMs > 0L)
        assertTrue(snapshot.ramRssFootprintMB > 0L)
        assertTrue(snapshot.activeAccelerator.isNotBlank())
        assertTrue(snapshot.totalOfflineEnergySavedMWh > 0.0)
    }

    @Test
    fun testNativeWhisperBridge_AudioTranscription() {
        runBlocking {
            val whisper = NativeWhisperBridge(context)
            val dummyPcm = ShortArray(16000 * 2) { 100 } // 2 seconds of 16kHz audio

            val result = whisper.transcribePcmBuffer(dummyPcm)
            assertTrue(result.text.isNotBlank())
            assertEquals("id", result.language)
            assertTrue(result.durationSeconds >= 2.0f)
            assertTrue(result.latencyMs > 0L)
            assertTrue(result.isOfflineNative)
        }
    }
}
