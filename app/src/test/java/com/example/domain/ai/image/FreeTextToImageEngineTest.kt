package com.example.domain.ai.image

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
class FreeTextToImageEngineTest {

    private lateinit var engine: FreeTextToImageEngine

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        engine = FreeTextToImageEngine(context)
    }

    @Test
    fun testFluxAndSDXLModelsAvailability() {
        val models = FreeTextToImageEngine.FREE_MODELS
        assertTrue("Harus menyediakan minimal 5 model AI gratis", models.size >= 5)

        // 1. Verify FLUX
        val fluxModel = models.find { it.id == "flux" }
        assertNotNull("Model FLUX.1 Schnell harus tersedia", fluxModel)
        assertEquals("FLUX.1 Schnell", fluxModel?.name)

        // 2. Verify SDXL
        val sdxlModel = models.find { it.id == "sdxl" }
        assertNotNull("Model Stable Diffusion XL (SDXL) harus tersedia", sdxlModel)
        assertEquals("Stable Diffusion XL (SDXL)", sdxlModel?.name)

        val turboModel = models.find { it.id == "turbo" }
        assertNotNull("Model SDXL Turbo harus tersedia", turboModel)

        val offlineModel = models.find { it.id == "offline-svg" }
        assertNotNull("Model Offline Vector harus tersedia", offlineModel)
    }

    @Test
    fun testFluxGeneration() = runBlocking {
        val result = engine.generateImage(
            prompt = "Katak di atas batu berlumut",
            style = "Cinematic Realistic",
            aspectRatio = "16:9",
            modelId = "flux",
            isOnline = true
        )

        assertNotNull(result)
        assertEquals("flux", result.modelId)
        assertTrue(result.imageUrl.contains("model=flux"))
        assertTrue(result.imageUrl.contains("width=1280") && result.imageUrl.contains("height=720"))
    }

    @Test
    fun testSDXLGeneration() = runBlocking {
        val result = engine.generateImage(
            prompt = "Lanskap Danau Toba",
            style = "Cinematic Realistic",
            aspectRatio = "1:1",
            modelId = "sdxl",
            isOnline = true
        )

        assertNotNull(result)
        assertEquals("sdxl", result.modelId)
        assertTrue(result.imageUrl.contains("model=turbo"))
    }
}
