package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personas")
data class PersonaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String,
    val description: String,
    val systemPrompt: String,
    val avatarEmoji: String,
    val temperature: Float = 0.7f,
    val isCustom: Boolean = false
)
