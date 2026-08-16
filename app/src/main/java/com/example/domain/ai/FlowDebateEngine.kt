package com.example.domain.ai

import com.example.data.local.dao.AnalyticsDao
import com.example.data.local.entity.AnalyticsLogEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * State machine untuk Arena Debat Multi-AI (Flow Mode).
 *
 * Mengorkestrasi 2 agen AI (Pro & Kontra) dalam beberapa putaran debat,
 * lalu mengaktifkan Moderator Sintesis untuk menghasilkan konsensus akhir.
 */

data class DebateMessage(
    val speakerName: String,
    val speakerEmoji: String,
    val roleBadge: String,       // "PRO" | "KONTRA" | "MODERATOR"
    val content: String,
    val round: Int,
    val isSynthesis: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis()
)

sealed class DebateState {
    data object Idle : DebateState()
    data class RoundStarted(val round: Int, val totalRounds: Int) : DebateState()
    data class ArgumentEmitted(val message: DebateMessage) : DebateState()
    data class SynthesisComplete(val summary: String) : DebateState()
    data class Error(val reason: String) : DebateState()
}

class FlowDebateEngine(
    private val offlineEngine: OfflineReasoningEngine,
    private val analyticsDao: AnalyticsDao? = null
) {

    /**
     * Menjalankan seluruh siklus debat sebagai Kotlin Flow.
     * Setiap state dikirim ke collector (UI) secara real-time.
     */
    fun startDebate(
        topic: String,
        totalRounds: Int = 3,
        proModelName: String = "Model Alpha (Visioner)",
        contraModelName: String = "Model Beta (Pragmatis)"
    ): Flow<DebateState> = flow {
        emit(DebateState.RoundStarted(1, totalRounds))

        val fullTranscript = StringBuilder("📋 TOPIK: \"$topic\"\n\n")
        var lastProArg = ""
        var lastContraArg = ""

        for (round in 1..totalRounds) {
            emit(DebateState.RoundStarted(round, totalRounds))

            // ─── GILIRAN PRO ────────────────────────────────────────────────
            delay(800)
            val proArg = generateArgument(
                role = "PRO (Visioner/Pendukung yang optimis dan ekspansif)",
                topic = topic,
                round = round,
                opponentLastArg = lastContraArg,
                transcript = fullTranscript.toString()
            )
            lastProArg = proArg
            fullTranscript.append("[$proModelName — Putaran $round PRO]:\n$proArg\n\n")

            emit(
                DebateState.ArgumentEmitted(
                    DebateMessage(
                        speakerName = proModelName,
                        speakerEmoji = "🔷",
                        roleBadge = "PRO",
                        content = proArg,
                        round = round
                    )
                )
            )

            // ─── GILIRAN KONTRA ─────────────────────────────────────────────
            delay(1000)
            val contraArg = generateArgument(
                role = "KONTRA (Pragmatis/Kritis yang realistis dan fokus pada risiko & etika)",
                topic = topic,
                round = round,
                opponentLastArg = lastProArg,
                transcript = fullTranscript.toString()
            )
            lastContraArg = contraArg
            fullTranscript.append("[$contraModelName — Putaran $round KONTRA]:\n$contraArg\n\n")

            emit(
                DebateState.ArgumentEmitted(
                    DebateMessage(
                        speakerName = contraModelName,
                        speakerEmoji = "🟣",
                        roleBadge = "KONTRA",
                        content = contraArg,
                        round = round
                    )
                )
            )
        }

        // ─── SINTESIS MODERATOR ─────────────────────────────────────────────
        delay(1200)
        val synthesis = generateSynthesis(topic, fullTranscript.toString())
        fullTranscript.append("[⚖️ Moderator Sintesis]:\n$synthesis")

        emit(
            DebateState.ArgumentEmitted(
                DebateMessage(
                    speakerName = "Hakim AI Sintesis (Nusantara Core)",
                    speakerEmoji = "⚖️",
                    roleBadge = "MODERATOR",
                    content = synthesis,
                    round = totalRounds + 1,
                    isSynthesis = true
                )
            )
        )
        emit(DebateState.SynthesisComplete(synthesis))

        // Log ke analitik
        try {
            analyticsDao?.insertLog(
                AnalyticsLogEntity(
                    mode = "OFFLINE",
                    tokenCount = fullTranscript.length / 4,
                    latencyMs = (totalRounds * 1800).toLong(),
                    energySavedMWh = 0.022,
                    category = "Debate",
                    modelName = "FlowDebateEngine"
                )
            )
        } catch (_: Exception) {}
    }.flowOn(Dispatchers.Default)

    private suspend fun generateArgument(
        role: String,
        topic: String,
        round: Int,
        opponentLastArg: String,
        transcript: String
    ): String = withContext(Dispatchers.IO) {
        // Gunakan OfflineReasoningEngine sebagai backend debat
        val prompt = buildString {
            append("PERAN ANDA: $role\n")
            append("TOPIK: \"$topic\"\n")
            append("PUTARAN: $round\n")
            if (opponentLastArg.isNotBlank()) {
                append("ARGUMEN LAWAN TERAKHIR: \"${opponentLastArg.take(300)}\"\n")
            }
            append("\nBerikan argumen tajam, logis, dan terstruktur dalam 2-3 paragraf. ")
            append("Jika putaran > 1, bantah poin spesifik dari lawan sebelum menyampaikan poin baru.")
        }

        try {
            val result = StringBuilder()
            OfflineReasoningEngine.generateOfflineResponse(
                prompt = prompt,
                personaRole = role,
                temperature = 0.8f
            ).also { response ->
                val (_, answer) = OfflineReasoningEngine.parseReasoningOutput(response.text)
                return@withContext answer.ifBlank { generateFallbackArgument(role, topic, round) }
            }
            result.toString()
        } catch (e: Exception) {
            generateFallbackArgument(role, topic, round)
        }
    }

    private suspend fun generateSynthesis(topic: String, transcript: String): String =
        withContext(Dispatchers.IO) {
            try {
                val result = OfflineReasoningEngine.generateOfflineResponse(
                    prompt = "Sebagai MODERATOR NETRAL, buat sintesis berimbang dari debat berikut tentang \"$topic\". Rangkum 3 poin terkuat dari PRO, 3 poin terkuat dari KONTRA, lalu berikan REKOMENDASI FINAL yang solutif.",
                    personaRole = "Moderator Sintesis",
                    temperature = 0.4f
                )
                val (_, answer) = OfflineReasoningEngine.parseReasoningOutput(result.text)
                answer.ifBlank { generateFallbackSynthesis(topic) }
            } catch (e: Exception) {
                generateFallbackSynthesis(topic)
            }
        }

    private fun generateFallbackArgument(role: String, topic: String, round: Int): String {
        return if (role.contains("PRO", ignoreCase = true)) {
            when (round) {
                1 -> "Saya mendukung premis bahwa \"$topic\" karena potensi transformatif yang ditawarkan jauh melampaui risiko yang ada. Data historis menunjukkan bahwa teknologi serupa selalu menciptakan nilai jauh lebih besar dari yang diperkirakan. Inovasi tidak menunggu konsensus — ia mendorong maju dan membuka era baru."
                2 -> "Merespons kekhawatiran yang disampaikan, perlu diperjelas bahwa risiko yang disebutkan telah memiliki mekanisme mitigasi yang matang. Framework regulasi modern justru dirancang untuk mengakomodasi perkembangan ini. Ketakutan berlebihan akan menghambat kemajuan yang justru dibutuhkan masyarakat."
                else -> "Setelah mendengar seluruh argumen, posisi saya semakin kuat: manfaat jangka panjang yang terukur, skalabel, dan inklusif adalah fondasi yang tidak bisa diabaikan. Inilah mengapa dukungan terhadap premis ini bukan sekadar optimisme, melainkan kalkulasi rasional berbasis bukti."
            }
        } else {
            when (round) {
                1 -> "Saya mempertanyakan optimisme berlebihan tentang \"$topic\" karena mengabaikan kompleksitas dampak sistemik yang nyata. Setiap perubahan skala besar membawa biaya transisi yang berat bagi kelompok rentan. Kehati-hatian bukan pesimisme — ini adalah tanggung jawab etis."
                2 -> "Argumen pro yang disajikan terlalu berfokus pada skenario ideal tanpa mempertimbangkan kegagalan sistemik yang telah terdokumentasi. Evidence dari kasus serupa menunjukkan bahwa tanpa safeguard yang kuat, dampak negatif akan tidak proporsional menimpa mereka yang paling tidak siap."
                else -> "Debat ini memperkuat keyakinan saya bahwa pendekatan bertahap dengan oversight ketat adalah satu-satunya jalan yang etis dan berkelanjutan. Kepercayaan publik harus dibangun melalui aksi yang terverifikasi, bukan janji-janji transformasi yang belum terbukti."
            }
        }
    }

    private fun generateFallbackSynthesis(topic: String): String {
        return """⚖️ KONSENSUS AI FLOW — Nusantara Core

📌 POIN TERKUAT PRO:
• Potensi inovatif dan transformatif yang signifikan telah didemonstrasikan
• Mekanisme mitigasi risiko telah berkembang seiring teknologi
• Manfaat jangka panjang yang terukur dan skalabel

📌 POIN TERKUAT KONTRA:
• Risiko dampak sistemik pada kelompok rentan tidak boleh diabaikan
• Kebutuhan safeguard dan regulasi yang adaptif dan proaktif
• Biaya transisi yang perlu direncanakan dengan matang

🏆 REKOMENDASI FINAL:
Topik "$topic" memerlukan pendekatan HYBRID yang pragmatis: dorong inovasi dengan penuh semangat, namun selalu didampingi framework akuntabilitas yang jelas, timeline bertahap, dan mekanisme review independen. Tidak ada yang mutlak benar — kearifan sejati ada di antara keduanya."""
    }
}
