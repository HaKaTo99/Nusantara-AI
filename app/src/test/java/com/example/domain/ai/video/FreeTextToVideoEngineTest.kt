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
    fun testFreeVideoModelsAvailability() {
        val models = FreeTextToVideoEngine.FREE_VIDEO_MODELS
        assertTrue("Harus menyediakan minimal 4 model video AI gratis", models.size >= 4)

        val animateDiff = models.find { it.id == "animatediff" }
        assertNotNull("Model AnimateDiff XL harus tersedia", animateDiff)
        assertEquals("AnimateDiff XL", animateDiff?.name)

        val cogVideo = models.find { it.id == "cogvideo" }
        assertNotNull("Model CogVideoX harus tersedia", cogVideo)

        val droneModel = models.find { it.id == "nusantara-drone" }
        assertNotNull("Model Nusantara Drone harus tersedia", droneModel)
    }

    @Test
    fun testVideoPromptEnrichment() {
        val rawPrompt = "Katak melompat di atas batu berlumut"
        val enriched = videoEngine.enrichVideoPrompt(
            userPrompt = rawPrompt,
            cameraMotion = "Pan Right",
            durationSec = 5,
            fps = 30,
            modelId = "animatediff"
        )

        assertTrue("Prompt harus memuat teks asli", enriched.contains("Katak melompat di atas batu berlumut"))
        assertTrue("Prompt harus memuat deskriptor gerakan kamera", enriched.contains("pan right"))
        assertTrue("Prompt harus memuat spesifikasi video", enriched.contains("5s video sequence") && enriched.contains("30 fps"))
    }

    @Test
    fun testGenerateVideoExecution() = runBlocking {
        val result = videoEngine.generateVideo(
            prompt = "Elang terbang di atas hutan",
            cameraMotion = "FPV Drone Dive",
            durationSec = 6,
            fps = 30,
            modelId = "nusantara-drone"
        )

        assertNotNull(result)
        assertEquals("nusantara-drone", result.modelId)
        assertEquals("FPV Drone Dive", result.cameraMotion)
        assertEquals(6, result.durationSec)
        assertTrue("URL video preview harus valid", result.videoPreviewUrl.startsWith("https://image.pollinations.ai/prompt/"))
        assertTrue("Keyframe URLs harus dihasilkan untuk multi-frame playback", result.keyframeUrls.size >= 3)
    }
}
