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
    fun testMathReasoningPrompt() = runBlocking {
        val response = hybridEngine.processQuery(
            prompt = "Hitung 25 * 4 + 100",
            selectedModel = "Gemini 3.5 Flash",
            modePreference = "OFFLINE"
        )

        assertNotNull(response)
        assertTrue("Harus memuat penalaran matematis", 
            response.text.contains("Matematis", ignoreCase = true) || response.text.contains("Kalkulasi", ignoreCase = true))
    }

    @Test
    fun testCodeGenerationPrompt() = runBlocking {
        val response = hybridEngine.processQuery(
            prompt = "Tulis fungsi kode Kotlin untuk memfilter data",
            selectedModel = "Gemini 3.5 Flash",
            modePreference = "OFFLINE"
        )

        assertNotNull(response)
        assertTrue("Harus memuat blok kode", response.text.contains("```") || response.codeArtifact != null)
    }

    @Test
    fun testGeneralQuestionPrompt() = runBlocking {
        val response = hybridEngine.processQuery(
            prompt = "Bagaimana cara kerja teknologi AI hybrid pada smartphone?",
            selectedModel = "Gemini 3.5 Flash",
            modePreference = "OFFLINE"
        )

        assertNotNull(response)
        assertTrue("Jawaban harus informatif dan terstruktur", response.text.length > 50)
        assertTrue("Reasoning steps harus dihasilkan", response.reasoningSteps.isNotEmpty())
    }
}
