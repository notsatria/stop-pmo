package dev.notsatria.stop_pmo.domain.model

data class RelapseEvent(
    val id: Int,
    val occurredAt: String, // ISO 8601 format date-time string
    val streak: Int, // Streak count at the time of relapse
    val note: String? // Optional notes about the relapse event
)
