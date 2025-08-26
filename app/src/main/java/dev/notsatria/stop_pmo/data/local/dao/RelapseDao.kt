package dev.notsatria.stop_pmo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import dev.notsatria.stop_pmo.data.local.entity.RelapseEventEntity

@Dao
interface RelapseDao {
    @Insert
    suspend fun insert(event: RelapseEventEntity)
}