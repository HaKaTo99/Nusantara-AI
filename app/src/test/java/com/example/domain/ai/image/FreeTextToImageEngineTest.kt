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
    fun testFreeModelsListAvailability() {
        val models = FreeTextToImageEngine.FREE_MODELS
        assertTrue("Harus menyediakan minimal 5 model AI gratis", models.size >= 5)

        val fluxModel = models.find { it.id == "flux" }
        assertNotNull("Model FLUX.1 Schnell harus tersedia", fluxModel)
        assertEquals("FLUX.1 Schnell", fluxModel?.name)

        val sdxlModel = models.find { it.id == "turbo" }
        assertNotNull("Model SDXL Turbo harus tersedia", sdxlModel)

        val offlineModel = models.find { it.id == "offline-svg" }
        assertNotNull("Model Offline Vector harus tersedia", offlineModel)
        assertTrue("Offline model harus berkemampuan offline", offlineModel?.isOfflineCapable == true)
    }

    @Test
    fun testPromptEnrichment() {
        val rawPrompt = "Candi Borobudur di malam hari"
        val enriched = engine.enrichPrompt(rawPrompt, "Cyberpunk Hologram", "flux")

        assertTrue("Prompt harus memuat teks asli", enriched.contains("Candi Borobudur di malam hari"))
        assertTrue("Prompt harus diperkaya dengan gaya visual", enriched.contains("cyberpunk aesthetic"))
        assertTrue("Prompt harus memuat kata kunci kualitas", enriched.contains("masterpiece"))
    }

    @Test
    fun testNusantaraHeritageCulturalPromptEnrichment() {
        val rawPrompt = "Pahlawan Nusantara berkuda"
        val enriched = engine.enrichPrompt(rawPrompt, "Batik Digital Art", "nusantara-heritage")

        assertTrue("Prompt harus memuat unsur budaya Nusantara", enriched.contains("Indonesian cultural"))
        assertTrue("Prompt harus memuat aksen batik", enriched.contains("batik"))
    }

    @Test
    fun testDimensionCalculations() {
        val (w1, h1) = engine.getDimensions("1:1")
        assertEquals(1024, w1)
        assertEquals(1024, h1)

        val (w2, h2) = engine.getDimensions("16:9")
        assertEquals(1280, w2)
        assertEquals(720, h2)

        val (w3, h3) = engine.getDimensions("9:16")
        assertEquals(720, w3)
        assertEquals(1280, h3)
    }

    @Test
    fun testGenerateImageOnlineUrlConstruction() = runBlocking {
        val result = engine.generateImage(
            prompt = "Kucing cyber memakai kacamata",
            style = "Cyberpunk Hologram",
            aspectRatio = "1:1",
            modelId = "flux",
            isOnline = true
        )

        assertNotNull(result)
        assertEquals("flux", result.modelId)
        assertTrue("URL gambar harus berasal dari Pollinations Free API", result.imageUrl.startsWith("https://image.pollinations.ai/prompt/"))
        assertTrue("URL gambar harus memuat parameter model dan resolusi", result.imageUrl.contains("model=flux") && result.imageUrl.contains("width=1024"))
        assertFalse("Hasil online tidak berupa SVG lokal", result.isOfflineSVG)
    }

    @Test
    fun testGenerateImageOfflineSVGFallback() = runBlocking {
        val result = engine.generateImage(
            prompt = "Gunung Bromo sunrise",
            style = "Cinematic Realistic",
            aspectRatio = "16:9",
            modelId = "offline-svg",
            isOnline = false
        )

        assertNotNull(result)
        assertTrue("Hasil offline harus bertipe SVG", result.isOfflineSVG)
        assertNotNull("Konten SVG tidak boleh null", result.svgContent)
        assertTrue("Konten harus memuat tag SVG valid", result.svgContent?.contains("<svg") == true)
        assertTrue("Konten SVG harus memuat judul visual Nusantara AI", result.svgContent?.contains("Nusantara AI") == true)
    }
}
