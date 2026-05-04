package com.notsatria.reboot.ui.screen.history

data class RelapseHistoryItem(
    val id: Int,
    val occurredAt: String,
    val streak: Int,
    val note: String?
)
