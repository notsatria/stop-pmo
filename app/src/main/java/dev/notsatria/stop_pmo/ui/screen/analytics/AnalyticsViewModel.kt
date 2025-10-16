package dev.notsatria.stop_pmo.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import dev.notsatria.stop_pmo.domain.repository.RelapseRepository
import dev.notsatria.stop_pmo.utils.formatDate
import dev.notsatria.stop_pmo.utils.formatDateOnly
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.sortedBy
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class AnalyticsViewModel(
    private val repository: RelapseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        viewModelScope.launch {
            repository.allRelapseFlow().collect { relapseEvents ->
                val streakData = calculateStreakData(relapseEvents)
                val chartData = processChartData(streakData)

                _uiState.update {
                    it.copy(
                        relapseEvents = relapseEvents,
                        streakData = streakData,
                        chartData = chartData,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun calculateStreakData(events: List<RelapseEvent>): List<StreakData> {
        if (events.isEmpty()) return emptyList()

        val sortedEvents = events.sortedBy { it.occurredAt }
        val streakList = mutableListOf<StreakData>()

        sortedEvents.forEachIndexed { index, event ->
            try {
                streakList.add(
                    StreakData(
                        relapseDate = event.occurredAt,
                        streakDays = event.streak,
                        index = index + 1
                    )
                )
            } catch (_: Exception) {
                // Skip invalid dates
            }
        }

        return streakList
    }

    private fun processChartData(streakData: List<StreakData>): List<ChartDataPoint> {
        return streakData.map { streak ->
            ChartDataPoint(
                x = streak.index.toFloat(),
                y = streak.streakDays.toFloat(),
                date = streak.relapseDate.formatDateOnly()
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun parseDate(dateString: String): LocalDate {
        val instant = Instant.parse(dateString)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    private fun calculateDaysBetween(startDate: LocalDate, endDate: LocalDate): Int {
        return abs((endDate.toEpochDays() - startDate.toEpochDays()).toInt())
    }
}
