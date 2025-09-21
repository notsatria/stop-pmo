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
}