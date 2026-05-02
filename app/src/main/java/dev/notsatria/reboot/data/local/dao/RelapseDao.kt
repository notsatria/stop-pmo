package dev.notsatria.stop_pmo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.notsatria.stop_pmo.data.local.entity.RelapseEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelapseDao {
    @Insert
    suspend fun insert(event: RelapseEventEntity)

    @Query("SELECT * FROM relapse_events ORDER BY occurredAt DESC LIMIT 1")
    suspend fun getLastRelapse(): RelapseEventEntity?

    @Query("SELECT * FROM relapse_events ORDER BY occurredAt DESC")
    fun getAllRelapsesFlow(): Flow<List<RelapseEventEntity>>

    @Query("DELETE FROM relapse_events")
    suspend fun clearAll()

    @Query("SELECT occurredAt FROM relapse_events ORDER BY occurredAt DESC LIMIT 1")
    fun getLastRelapseTimeFlow(): Flow<String?>

    @Query("SELECT * FROM relapse_events ORDER BY occurredAt DESC LIMIT :count")
    fun recentRelapses(count: Int): Flow<List<RelapseEventEntity>>

    @Query("SELECT * FROM relapse_events ORDER BY occurredAt DESC LIMIT :count OFFSET :offset")
    fun getRelapseHistory(count: Int, offset: Int): Flow<List<RelapseEventEntity>>

    @Query("UPDATE relapse_events SET streak = :streak WHERE id = (SELECT id FROM relapse_events ORDER BY occurredAt DESC LIMIT 1 OFFSET 1)")
    suspend fun updatePreviousStreak(streak: Int)
}