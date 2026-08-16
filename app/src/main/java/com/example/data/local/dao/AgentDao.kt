package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {

    @Query("SELECT * FROM agents ORDER BY lastActiveMs DESC")
    fun getAllAgents(): Flow<List<AgentEntity>>

    @Query("SELECT * FROM agents WHERE status = 'RUNNING' ORDER BY lastActiveMs DESC")
    fun getRunningAgents(): Flow<List<AgentEntity>>

    @Query("SELECT COUNT(*) FROM agents WHERE status = 'RUNNING'")
    fun getRunningAgentCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: AgentEntity): Long

    @Update
    suspend fun updateAgent(agent: AgentEntity)

    @Query("UPDATE agents SET status = :status, lastActiveMs = :timestamp WHERE id = :agentId")
    suspend fun updateAgentStatus(agentId: Long, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE agents SET progress = :progress WHERE id = :agentId")
    suspend fun updateAgentProgress(agentId: Long, progress: Int)

    @Query("UPDATE agents SET tasksCompleted = tasksCompleted + 1, status = 'COMPLETED', progress = 100 WHERE id = :agentId")
    suspend fun markAgentCompleted(agentId: Long)

    @Query("DELETE FROM agents WHERE id = :agentId")
    suspend fun deleteAgent(agentId: Long)

    @Query("DELETE FROM agents")
    suspend fun clearAllAgents()
}
