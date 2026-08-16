package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AnalyticsLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM analytics_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AnalyticsLogEntity>>

    @Query("SELECT * FROM analytics_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentLogs(): Flow<List<AnalyticsLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AnalyticsLogEntity): Long

    @Query("SELECT SUM(tokenCount) FROM analytics_logs")
    fun getTotalTokens(): Flow<Int?>

    @Query("SELECT AVG(latencyMs) FROM analytics_logs")
    fun getAverageLatency(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM analytics_logs WHERE mode = 'OFFLINE'")
    fun getOfflineQueryCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analytics_logs WHERE mode = 'ONLINE'")
    fun getOnlineQueryCount(): Flow<Int>

    @Query("DELETE FROM analytics_logs")
    suspend fun clearAllLogs()
}
