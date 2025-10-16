package dev.notsatria.stop_pmo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("relapse_events")
data class RelapseEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val occurredAt: String, // ISO 8601 format date-time string
    val streak: Int, // Streak count at the time of relapse
    val note: String? // Optional notes about the relapse event
)