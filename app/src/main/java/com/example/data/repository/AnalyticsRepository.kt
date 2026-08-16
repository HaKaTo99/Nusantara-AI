package com.example.data.repository

import com.example.data.local.dao.AnalyticsDao
import com.example.data.local.entity.AnalyticsLogEntity
import kotlinx.coroutines.flow.Flow

class AnalyticsRepository(private val analyticsDao: AnalyticsDao) {
    val allLogs: Flow<List<AnalyticsLogEntity>> = analyticsDao.getAllLogs()
    val recentLogs: Flow<List<AnalyticsLogEntity>> = analyticsDao.getRecentLogs()
    val totalTokens: Flow<Int?> = analyticsDao.getTotalTokens()
    val avgLatency: Flow<Double?> = analyticsDao.getAverageLatency()
    val offlineCount: Flow<Int> = analyticsDao.getOfflineQueryCount()
    val onlineCount: Flow<Int> = analyticsDao.getOnlineQueryCount()

    suspend fun logEvent(log: AnalyticsLogEntity) = analyticsDao.insertLog(log)
    suspend fun clearLogs() = analyticsDao.clearAllLogs()
}
