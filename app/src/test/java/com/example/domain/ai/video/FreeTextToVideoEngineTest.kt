package com.example.domain.ai.video

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class FreeTextToVideoEngineTest {

    private lateinit var videoEngine: FreeTextToVideoEngine

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        videoEngine = FreeTextToVideoEngine(context)
    }

    @Test
    fun testFreeCinemaModelsAvailability() {
        val models = FreeTextToVideoEngine.FREE_CINEMA_MODELS
        assertTrue("Harus menyediakan minimal 5 model cinema AI gratis", models.size >= 5)

        // 1. Verify AnimateDiff
        val animateDiff = models.find { it.id == "animatediff" }
        assertNotNull("Model AnimateDiff XL harus tersedia", animateDiff)
        assertEquals("AnimateDiff XL", animateDiff?.name)
        assertTrue(animateDiff?.isFree == true)

        // 2. Verify CogVideoX
        val cogVideo = models.find { it.id == "cogvideo" }
        assertNotNull("Model CogVideoX harus tersedia", cogVideo)
        assertEquals("CogVideoX Motion", cogVideo?.name)
        assertTrue(cogVideo?.isFree == true)

        // 3. Verify SVD
        val svd = models.find { it.id == "svd" }
        assertNotNull("Model Stable Video Diffusion harus tersedia", svd)
        assertEquals("Stable Video Diffusion (SVD)", svd?.name)
        assertTrue(svd?.isFree == true)

        val droneModel = models.find { it.id == "nusantara-drone" }
        assertNotNull("Model Nusantara Drone harus tersedia", droneModel)
    }

    @Test
    fun testAnimateDiffGeneration() = runBlocking {
        val result = videoEngine.generateVideo(
            prompt = "Air terjun mengalir deras di hutan tropis",
            cameraMotion = "Pan Right",
            aspectRatio = "16:9",
            durationSec = 5,
            fps = 30,
            modelId = "animatediff",
            mode = "T2V"
        )

        assertNotNull(result)
        assertEquals("animatediff", result.modelId)
        assertEquals("AnimateDiff XL", result.modelName)
        assertTrue(result.keyframeUrls.size >= 4)
    }

    @Test
    fun testCogVideoXGeneration() = runBlocking {
        val result = videoEngine.generateVideo(
            prompt = "Mobil sport meluncur di jalan tol IKN",
            cameraMotion = "Dynamic Orbit",
            aspectRatio = "21:9",
            durationSec = 6,
            fps = 30,
            modelId = "cogvideo",
            mode = "T2V"
        )

        assertNotNull(result)
        assertEquals("cogvideo", result.modelId)
        assertEquals("CogVideoX Motion", result.modelName)
    }

    @Test
    fun testSVDGeneration() = runBlocking {
        val result = videoEngine.generateVideo(
            prompt = "Animasi potret wajah tersenyum alami",
            cameraMotion = "Zoom In (Dolly)",
            aspectRatio = "1:1",
            durationSec = 5,
            fps = 30,
            modelId = "svd",
            mode = "I2V"
        )

        assertNotNull(result)
        assertEquals("svd", result.modelId)
        assertEquals("Stable Video Diffusion (SVD)", result.modelName)
        assertEquals("I2V", result.mode)
    }
}
