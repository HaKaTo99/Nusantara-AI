package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val sender: String, // "USER", "AI", "SYSTEM"
    val content: String,
    val reasoningStepsJson: String = "", // JSON list of step strings
    val tokenCount: Int = 0,
    val latencyMs: Long = 0L,
    val isOfflineGenerated: Boolean = false,
    val syncStatus: String = "SYNCED", // "SYNCED", "PENDING_SYNC", "LOCAL_ONLY"
    val timestamp: Long = System.currentTimeMillis(),
    val attachmentsJson: String = "",
    val isEncrypted: Boolean = true,
    val modelUsed: String = "Nusantara-Hybrid"
)
