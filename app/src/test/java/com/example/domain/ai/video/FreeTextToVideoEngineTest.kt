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

        val animateDiff = models.find { it.id == "animatediff" }
        assertNotNull("Model AnimateDiff XL harus tersedia", animateDiff)
        assertEquals("AnimateDiff XL", animateDiff?.name)
        assertTrue(animateDiff?.isFree == true)

        val cogVideo = models.find { it.id == "cogvideo" }
        assertNotNull("Model CogVideoX harus tersedia", cogVideo)

        val svd = models.find { it.id == "svd" }
        assertNotNull("Model Stable Video Diffusion harus tersedia", svd)

        val droneModel = models.find { it.id == "nusantara-drone" }
        assertNotNull("Model Nusantara Drone harus tersedia", droneModel)
    }

    @Test
    fun testCinematicDimensions() {
        val (w16, h16) = videoEngine.calculateDimensions("16:9")
        assertEquals(1280, w16)
        assertEquals(720, h16)

        val (w9, h9) = videoEngine.calculateDimensions("9:16")
        assertEquals(720, w9)
        assertEquals(1280, h9)

        val (w21, h21) = videoEngine.calculateDimensions("21:9")
        assertEquals(1536, w21)
        assertEquals(640, h21)
    }

    @Test
    fun testVideoPromptEnrichment() {
        val rawPrompt = "Katak melompat di atas batu berlumut"
        val enriched = videoEngine.enrichVideoPrompt(
            userPrompt = rawPrompt,
            cameraMotion = "Pan Right",
            aspectRatio = "16:9",
            durationSec = 5,
            fps = 30,
            modelId = "animatediff",
            mode = "T2V"
        )

        assertTrue("Prompt harus memuat teks asli", enriched.contains("Katak melompat di atas batu berlumut"))
        assertTrue("Prompt harus memuat deskriptor gerakan kamera", enriched.contains("pan right"))
        assertTrue("Prompt harus memuat rasio aspek", enriched.contains("16:9 aspect ratio"))
        assertTrue("Prompt harus memuat spesifikasi video", enriched.contains("5s video sequence") && enriched.contains("30 fps"))
    }

    @Test
    fun testGenerateVideoExecution() = runBlocking {
        val result = videoEngine.generateVideo(
            prompt = "Elang terbang di atas hutan",
            cameraMotion = "FPV Drone Dive",
            aspectRatio = "16:9",
            durationSec = 6,
            fps = 30,
            modelId = "nusantara-drone",
            mode = "T2V"
        )

        assertNotNull(result)
        assertEquals("nusantara-drone", result.modelId)
        assertEquals("FPV Drone Dive", result.cameraMotion)
        assertEquals("16:9", result.aspectRatio)
        assertEquals(6, result.durationSec)
        assertTrue("URL video preview harus valid", result.videoPreviewUrl.startsWith("https://image.pollinations.ai/prompt/"))
        assertTrue("Keyframe URLs harus dihasilkan untuk multi-frame playback", result.keyframeUrls.size >= 4)
    }
}
