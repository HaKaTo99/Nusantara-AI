package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics_logs")
data class AnalyticsLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: String, // "ONLINE", "OFFLINE"
    val tokenCount: Int,
    val latencyMs: Long,
    val energySavedMWh: Double,
    val category: String, // "Coding", "Writing", "Analysis", "Translation", "Reasoning", "General"
    val modelName: String
)
