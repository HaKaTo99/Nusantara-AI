package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val modelName: String = "Gemini 3.5 Flash",
    val mode: String = "HYBRID", // ONLINE, OFFLINE, HYBRID
    val personaId: Long = 0, // 0 = Default AI
    val isEncrypted: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastMessagePreview: String = "",
    val isSynced: Boolean = true
)
