package com.example

import com.example.domain.ai.LocalModelScanner
import com.example.domain.ai.OfflineReasoningEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun test_offline_reasoning_generation() {
    val response = OfflineReasoningEngine.generateOfflineResponse(
      prompt = "Apa itu komputasi terdistribusi?",
      personaRole = "Nusantara Core AI",
      temperature = 0.7f
    )
    assertNotNull(response)
    assertTrue(response.text.isNotBlank())
    assertTrue(response.isOffline)
    assertTrue(response.reasoningSteps.isNotEmpty())
  }

  @Test
  fun test_cot_parsing() {
    val raw = "[thinking]Memeriksa parameter keamanan[/thinking]Jawaban terverifikasi."
    val (thinking, answer) = OfflineReasoningEngine.parseReasoningOutput(raw)
    assertEquals("Memeriksa parameter keamanan", thinking)
    assertEquals("Jawaban terverifikasi.", answer)
  }

  @Test
  fun test_confidence_detection() {
    val scoreHigh = OfflineReasoningEngine.detectConfidence(
      responseText = "Ini adalah penjelasan yang sangat mendalam dan terstruktur dengan data 2026.\nTermasuk rincian teknis lengkap.",
      isOnline = true,
      latencyMs = 350
    )
    assertTrue(scoreHigh >= 75)

    val scoreLow = OfflineReasoningEngine.detectConfidence(
      responseText = "Ya.",
      isOnline = false,
      latencyMs = 1200
    )
    assertTrue(scoreLow < 70)
  }

  @Test
  fun test_model_scanner_extensions() {
    assertTrue(LocalModelScanner.isModelFile("qwen-7b-q4.gguf"))
    assertTrue(LocalModelScanner.isModelFile("gemma-2b.bin"))
    assertTrue(LocalModelScanner.isModelFile("whisper-tiny.onnx"))
    assertTrue(LocalModelScanner.isModelFile("mobilenet.tflite"))
    assertFalse(LocalModelScanner.isModelFile("photo.png"))
    assertFalse(LocalModelScanner.isModelFile("document.pdf"))
  }
}
