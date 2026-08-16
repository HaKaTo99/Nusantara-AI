package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity untuk tabel agents — menyimpan agen AI 24/7 yang berjalan di background.
 */
@Entity(tableName = "agents")
data class AgentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val avatarEmoji: String = "🤖",
    val status: String = "IDLE",          // "RUNNING", "IDLE", "COMPLETED", "ERROR", "PAUSED"
    val taskType: String = "GENERAL",     // "EMAIL", "CALENDAR", "REPORT", "RESEARCH", "MONITOR"
    val progress: Int = 0,                // 0-100
    val lastActiveMs: Long = System.currentTimeMillis(),
    val tasksCompleted: Int = 0,
    val isEnabled: Boolean = true,
    val scheduleConfig: String = ""       // JSON config for scheduling
)
