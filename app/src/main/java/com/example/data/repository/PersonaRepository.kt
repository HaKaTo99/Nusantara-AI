package com.example.data.repository

import com.example.data.local.dao.PersonaDao
import com.example.data.local.entity.PersonaEntity
import kotlinx.coroutines.flow.Flow

class PersonaRepository(private val personaDao: PersonaDao) {
    val allPersonas: Flow<List<PersonaEntity>> = personaDao.getAllPersonas()

    suspend fun getPersonaById(id: Long): PersonaEntity? = personaDao.getPersonaById(id)

    suspend fun createCustomPersona(
        name: String,
        role: String,
        description: String,
        systemPrompt: String,
        avatarEmoji: String,
        temperature: Float
    ): Long {
        val persona = PersonaEntity(
            name = name,
            role = role,
            description = description,
            systemPrompt = systemPrompt,
            avatarEmoji = avatarEmoji,
            temperature = temperature,
            isCustom = true
        )
        return personaDao.insertPersona(persona)
    }

    suspend fun deleteCustomPersona(id: Long) {
        personaDao.deleteCustomPersona(id)
    }
}
