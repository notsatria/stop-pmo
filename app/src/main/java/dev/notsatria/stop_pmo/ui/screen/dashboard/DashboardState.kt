package dev.notsatria.stop_pmo.ui.screen.dashboard

import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import kotlin.time.Duration

data class DashboardState(
    val elapsedTime: Duration = Duration.ZERO,
    val recentRelapses: List<RelapseEvent> = emptyList(),
)
