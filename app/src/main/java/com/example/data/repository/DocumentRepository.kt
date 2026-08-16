package com.example.data.repository

import com.example.data.local.dao.DocumentDao
import com.example.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

class DocumentRepository(private val documentDao: DocumentDao) {
    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments()

    suspend fun getDocumentById(id: Long): DocumentEntity? = documentDao.getDocumentById(id)

    suspend fun saveDocument(
        title: String,
        fileType: String,
        content: String,
        summary: String,
        keyInsights: String
    ): Long {
        val doc = DocumentEntity(
            title = title,
            fileType = fileType,
            content = content,
            summary = summary,
            keyInsights = keyInsights,
            tokenSize = content.length / 4,
            isEncrypted = true,
            createdAt = System.currentTimeMillis()
        )
        return documentDao.insertDocument(doc)
    }

    suspend fun deleteDocument(id: Long) = documentDao.deleteDocument(id)
}
