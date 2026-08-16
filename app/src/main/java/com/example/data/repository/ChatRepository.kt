package com.example.data.repository

import com.example.data.local.dao.ChatDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ChatSessionEntity
import com.example.domain.crypto.EncryptionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(private val chatDao: ChatDao) {

    val allSessions: Flow<List<ChatSessionEntity>> = chatDao.getAllSessions()

    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>> {
        return chatDao.getMessagesForSession(sessionId).map { list ->
            list.map { msg ->
                // Decrypt content for display
                if (msg.isEncrypted) {
                    msg.copy(content = EncryptionManager.decrypt(msg.content))
                } else {
                    msg
                }
            }
        }
    }

    suspend fun createSession(
        title: String,
        modelName: String = "Gemini 3.5 Flash",
        mode: String = "HYBRID",
        personaId: Long = 1
    ): Long {
        val session = ChatSessionEntity(
            title = title,
            modelName = modelName,
            mode = mode,
            personaId = personaId,
            isEncrypted = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastMessagePreview = "",
            isSynced = true
        )
        return chatDao.insertSession(session)
    }

    suspend fun saveMessage(
        sessionId: Long,
        sender: String,
        content: String,
        reasoningStepsJson: String = "",
        tokenCount: Int = 0,
        latencyMs: Long = 0L,
        isOfflineGenerated: Boolean = false,
        syncStatus: String = "SYNCED",
        modelUsed: String = "Nusantara-Hybrid",
        encrypt: Boolean = true
    ): Long {
        val storedContent = if (encrypt) EncryptionManager.encrypt(content) else content
        val msg = ChatMessageEntity(
            sessionId = sessionId,
            sender = sender,
            content = storedContent,
            reasoningStepsJson = reasoningStepsJson,
            tokenCount = tokenCount,
            latencyMs = latencyMs,
            isOfflineGenerated = isOfflineGenerated,
            syncStatus = syncStatus,
            timestamp = System.currentTimeMillis(),
            isEncrypted = encrypt,
            modelUsed = modelUsed
        )
        val msgId = chatDao.insertMessage(msg)

        // Update session's last message & time
        val session = chatDao.getSessionById(sessionId)
        if (session != null) {
            val preview = content.take(60)
            chatDao.updateSession(
                session.copy(
                    lastMessagePreview = preview,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        return msgId
    }

    suspend fun deleteSession(sessionId: Long) {
        chatDao.deleteMessagesForSession(sessionId)
        chatDao.deleteSession(sessionId)
    }

    suspend fun markAllSynced() {
        chatDao.markAllSynced()
    }
}
