package com.notsatria.reboot.ui.screen.dashboard

import com.notsatria.reboot.domain.model.RelapseEvent
import kotlin.time.Duration

data class DashboardState(
    val elapsedTime: Duration = Duration.ZERO,
    val recentRelapses: List<RelapseEvent> = emptyList(),
    val currentStreak: Int = 0
)
