package dev.notsatria.stop_pmo.ui.screen.analytics

import dev.notsatria.stop_pmo.domain.model.RelapseEvent

data class AnalyticsState(
    val relapseEvents: List<RelapseEvent> = emptyList(),
    val isLoading: Boolean = true,
    val chartData: List<ChartDataPoint> = emptyList(),
    val streakData: List<StreakData> = emptyList()
)

data class ChartDataPoint(
    val x: Float,
    val y: Float,
    val date: String
)

data class StreakData(
    val relapseDate: String,
    val streakDays: Int,
    val index: Int
)
