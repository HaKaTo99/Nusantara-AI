package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val fileType: String, // "CSV", "PDF", "TXT", "JSON"
    val content: String,
    val summary: String,
    val keyInsights: String, // Delimited or JSON insights
    val tokenSize: Int = 0,
    val isEncrypted: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
