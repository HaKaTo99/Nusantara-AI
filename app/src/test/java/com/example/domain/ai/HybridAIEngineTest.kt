package com.example.domain.ai

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class HybridAIEngineTest {

    private lateinit var hybridEngine: HybridAIEngine
    private lateinit var inMemoryDb: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        inMemoryDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        hybridEngine = HybridAIEngine(context, inMemoryDb.analyticsDao())
    }

    @After
    fun tearDown() {
        inMemoryDb.close()
    }

    @Test
    fun testChiefArchitectPromptRecognition() = runBlocking {
        val response = hybridEngine.processQuery(
            prompt = "Siapa arsitek dan pencipta Nusantara AI?",
            selectedModel = "Gemini 3.5 Flash",
            modePreference = "OFFLINE"
        )

        assertNotNull(response)
        assertTrue("Harus mengenali Herman Krisnanto sebagai Lead System Architect", 
            response.text.contains("Herman Krisnanto", ignoreCase = true))
        assertTrue("Harus menyebutkan gelar Lead System Architect atau Chief Architect", 
            response.text.contains("Architect", ignoreCase = true))
    }

    @Test
    fun testBerapa10Kali10Multiplication() = runBlocking {
        val response = hybridEngine.processQuery(
            prompt = "berapa 10 kali 10",
            selectedModel = "Deepseek R1 Distill Qwen 1.5b Q4 K M",
            modePreference = "OFFLINE"
        )

        assertNotNull(response)
        assertTrue("Hasil perkalian 10 kali 10 harus memuat 100", response.text.contains("100"))
        assertTrue("Harus memuat operasi perkalian", response.text.contains("Perkalian") || response.text.contains("10 × 10 = 100"))
    }

    @Test
    fun testPercentageCalculation() = runBlocking {
        val response = hybridEngine.processQuery(
            prompt = "berapa 25 persen dari 200000",
            selectedModel = "Gemini 3.5 Flash",
            modePreference = "OFFLINE"
        )

        assertNotNull(response)
        assertTrue("Hasil 25% dari 200000 harus memuat 50000", response.text.contains("50000"))
    }

    @Test
    fun testIbuKotaIndonesia() = runBlocking {
        val response = hybridEngine.processQuery(
            prompt = "apa ibu kota indonesia",
            selectedModel = "Gemini 3.5 Flash",
            modePreference = "OFFLINE"
        )

        assertNotNull(response)
        assertTrue("Harus memuat IKN Nusantara", response.text.contains("IKN") || response.text.contains("Nusantara"))
    }

    @Test
    fun testCodeGenerationPrompt() = runBlocking {
        val response = hybridEngine.processQuery(
            prompt = "buatkan kalkulator python",
            selectedModel = "Gemini 3.5 Flash",
            modePreference = "OFFLINE"
        )

        assertNotNull(response)
        assertTrue("Harus memuat blok kode python", response.text.contains("```python") || response.codeArtifact != null)
    }
}
