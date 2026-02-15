package dev.notsatria.stop_pmo.ui.screen.history

data class RelapseHistoryItem(
    val id: Int,
    val occurredAt: String,
    val streak: Int,
    val note: String?
)
