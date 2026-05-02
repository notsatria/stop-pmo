package dev.notsatria.stop_pmo.ui.screen.analytics

import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import kotlinx.datetime.LocalDate

data class AnalyticsState(
    val relapseEvents: List<RelapseEvent> = emptyList(),
    val isLoading: Boolean = true,
    val chartData: List<ChartDataPoint> = emptyList(),
    val streakData: List<StreakData> = emptyList(),
    val heatmapData: List<HeatmapDay> = emptyList(),
    val selectedFilter: DateFilter = DateFilter.ALL,
)

data class ChartDataPoint(
    val y: Float,
    val date: String
)

data class StreakData(
    val relapseDate: String,
    val streakDays: Int,
)

data class HeatmapDay(
    val date: LocalDate,
    val streakDays: Int,
    val isRelapse: Boolean,
)

enum class DateFilter(val label: String, val days: Int?) {
    DAYS_30("30d", 30),
    DAYS_90("90d", 90),
    MONTHS_6("6m", 180),
    YEAR_1("1y", 365),
    ALL("All", null);
}
