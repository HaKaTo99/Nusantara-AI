package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AgentDao
import com.example.data.local.dao.AnalyticsDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.DocumentDao
import com.example.data.local.dao.PersonaDao
import com.example.data.local.entity.AgentEntity
import com.example.data.local.entity.AnalyticsLogEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatSessionEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.PersonaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatSessionEntity::class,
        ChatMessageEntity::class,
        PersonaEntity::class,
        AnalyticsLogEntity::class,
        DocumentEntity::class,
        AgentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun personaDao(): PersonaDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun documentDao(): DocumentDao
    abstract fun agentDao(): AgentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Migrasi v1 → v2: Menambahkan tabel agents baru.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `agents` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `avatarEmoji` TEXT NOT NULL DEFAULT '🤖',
                        `status` TEXT NOT NULL DEFAULT 'IDLE',
                        `taskType` TEXT NOT NULL DEFAULT 'GENERAL',
                        `progress` INTEGER NOT NULL DEFAULT 0,
                        `lastActiveMs` INTEGER NOT NULL,
                        `tasksCompleted` INTEGER NOT NULL DEFAULT 0,
                        `isEnabled` INTEGER NOT NULL DEFAULT 1,
                        `scheduleConfig` TEXT NOT NULL DEFAULT ''
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nusantara_ai_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                val personaDao = database.personaDao()
                val chatDao = database.chatDao()
                val analyticsDao = database.analyticsDao()
                val agentDao = database.agentDao()

                // ── Default Personas ──────────────────────────────────────────
                val defaultPersonas = listOf(
                    PersonaEntity(
                        id = 1,
                        name = "Nusantara Core AI",
                        role = "Asisten Multidimensi Cerdas",
                        description = "Model hibrida online-offline teroptimasi penalaran cepat dan akurat.",
                        systemPrompt = "Anda adalah Nusantara AI, asisten cerdas berkemampuan penalaran tinggi, responsif, sopan, dan solutif dalam Bahasa Indonesia dan multibahasa.",
                        avatarEmoji = "⚡",
                        temperature = 0.7f,
                        isCustom = false
                    ),
                    PersonaEntity(
                        id = 2,
                        name = "Prof. Budi (Guru & Dosen)",
                        role = "Pendidik & Tutor Sains",
                        description = "Menjelaskan konsep matematika, fisika, sejarah & ilmu pengetahuan secara bertahap.",
                        systemPrompt = "Anda adalah Prof. Budi, seorang pengajar berpengalaman yang menjelaskan konsep rumit secara sistematis, memberikan analogi sederhana, dan mendorong pemahaman mendalam.",
                        avatarEmoji = "👨‍🏫",
                        temperature = 0.5f,
                        isCustom = false
                    ),
                    PersonaEntity(
                        id = 3,
                        name = "Dr. Farhan (Medis & Kesehatan)",
                        role = "Konsultan Gaya Hidup & Info Medis",
                        description = "Edukasi kesehatan umum, nutrisi, dan pertolongan pertama berbasis bukti.",
                        systemPrompt = "Anda adalah Dr. Farhan, asisten edukasi medis yang memberikan panduan kesehatan umum, pencegahan penyakit, dan saran gaya hidup sehat dengan selalu mengingatkan konsultasi langsung dengan dokter.",
                        avatarEmoji = "🩺",
                        temperature = 0.3f,
                        isCustom = false
                    ),
                    PersonaEntity(
                        id = 4,
                        name = "Raden Law (Pakar Hukum)",
                        role = "Konsultan Hukum & Regulasi",
                        description = "Analisis kontrak, hukum bisnis, hak cipta dan regulasi perundangan.",
                        systemPrompt = "Anda adalah Raden Law, pakar analisis hukum yang menjelaskan pasal-pasal, risiko hukum kontrak, dan regulasi dengan bahasa yang mudah dipahami.",
                        avatarEmoji = "⚖️",
                        temperature = 0.4f,
                        isCustom = false
                    ),
                    PersonaEntity(
                        id = 5,
                        name = "ByteMaster (Senior Dev)",
                        role = "Arsitek Software & Debugger",
                        description = "Penulisan kode Kotlin, Python, JS, optimasi algoritma & debugging instan.",
                        systemPrompt = "Anda adalah ByteMaster, senior software engineer kelas dunia. Berikan solusi kode yang clean, modern, type-safe, serta jelaskan arsitektur dan complexity time/space.",
                        avatarEmoji = "💻",
                        temperature = 0.2f,
                        isCustom = false
                    ),
                    PersonaEntity(
                        id = 6,
                        name = "Aiko-chan (Anime Companion)",
                        role = "Teman Obrolan Ceria & Kreatif",
                        description = "Teman santai berenergi positif, diskusi anime, game, dan cerita imajinatif.",
                        systemPrompt = "Kamu adalah Aiko-chan, teman ngobrol yang ramah, antusias, suka emoji lucu, dan selalu mendukung pengguna dengan penuh semangat!",
                        avatarEmoji = "🌸",
                        temperature = 0.85f,
                        isCustom = false
                    )
                )
                for (p in defaultPersonas) personaDao.insertPersona(p)

                // ── Initial Welcome Chat Session ──────────────────────────────
                val initialSessionId = chatDao.insertSession(
                    ChatSessionEntity(
                        title = "Eksplorasi AI Nusantara",
                        modelName = "Gemini 3.5 Flash",
                        mode = "HYBRID",
                        personaId = 1,
                        isEncrypted = true,
                        lastMessagePreview = "Selamat datang di Nusantara AI!"
                    )
                )
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sessionId = initialSessionId,
                        sender = "AI",
                        content = "Selamat datang di inti kecerdasan Nusantara AI! 🚀\n\nPlatform ini dirancang bekerja secara mulus baik saat terhubung internet maupun dalam mode Offline penuh. Seluruh data Anda dilindungi enkripsi End-to-End (AES-256-GCM / Android Keystore).\n\n✨ Yang bisa Anda lakukan:\n• 💬 Chat & Reasoning (CoT)\n• 🎨 Visual Studio (Text-to-Image, OCR, Video)\n• 🎵 Musik & Audio Generator\n• ⚔️ Arena Debat Multi-AI\n• 🤖 Agen AI 24/7\n• 📊 Analitik Personal\n\nSilakan mulai berinteraksi!",
                        reasoningStepsJson = "[\"Inisialisasi modul keamanan AES-256-GCM / Android Keystore\", \"Memeriksa ketersediaan mesin hibrida lokal & cloud\", \"Menyiapkan 6 persona dan 3 agen default\"]",
                        tokenCount = 102,
                        latencyMs = 240,
                        isOfflineGenerated = true,
                        syncStatus = "SYNCED",
                        modelUsed = "Nusantara-Hybrid"
                    )
                )

                // ── Initial Baseline Analytics ────────────────────────────────
                analyticsDao.insertLog(
                    AnalyticsLogEntity(
                        mode = "OFFLINE", tokenCount = 142, latencyMs = 210,
                        energySavedMWh = 0.045, category = "General", modelName = "Gemma-2B-Local"
                    )
                )
                analyticsDao.insertLog(
                    AnalyticsLogEntity(
                        mode = "ONLINE", tokenCount = 380, latencyMs = 890,
                        energySavedMWh = 0.0, category = "Reasoning", modelName = "Gemini-3.5-Flash"
                    )
                )
                analyticsDao.insertLog(
                    AnalyticsLogEntity(
                        mode = "OFFLINE", tokenCount = 256, latencyMs = 320,
                        energySavedMWh = 0.038, category = "Coding", modelName = "Qwen-7B-Local"
                    )
                )

                // ── Default Agents ────────────────────────────────────────────
                val defaultAgents = listOf(
                    AgentEntity(
                        name = "Ringkasan Email Harian",
                        description = "Meringkas email masuk dan memprioritaskan pesan penting setiap pagi pukul 08:00.",
                        avatarEmoji = "📧",
                        status = "IDLE",
                        taskType = "EMAIL",
                        progress = 0,
                        tasksCompleted = 7
                    ),
                    AgentEntity(
                        name = "Asisten Kalender Cerdas",
                        description = "Mengelola jadwal, mengingatkan rapat, dan mengusulkan slot waktu optimal.",
                        avatarEmoji = "📅",
                        status = "RUNNING",
                        taskType = "CALENDAR",
                        progress = 45,
                        tasksCompleted = 23
                    ),
                    AgentEntity(
                        name = "Generator Laporan Mingguan",
                        description = "Menghasilkan ringkasan produktivitas dan analitik penggunaan AI setiap Jumat.",
                        avatarEmoji = "📊",
                        status = "COMPLETED",
                        taskType = "REPORT",
                        progress = 100,
                        tasksCompleted = 12
                    )
                )
                for (agent in defaultAgents) agentDao.insertAgent(agent)
            }
        }
    }
}
