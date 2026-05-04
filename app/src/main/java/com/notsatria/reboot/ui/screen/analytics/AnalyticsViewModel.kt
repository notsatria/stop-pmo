package com.notsatria.reboot.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.reboot.domain.model.RelapseEvent
import com.notsatria.reboot.domain.repository.RelapseRepository
import com.notsatria.reboot.utils.calculateCurrentStreak
import com.notsatria.reboot.utils.dateFormat2
import com.notsatria.reboot.utils.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

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
                _uiState.update { it.copy(isLoading = true) }
                val streakData = calculateStreakData(relapseEvents)
                val selectedFilter = _uiState.value.selectedFilter
                val chartData = withContext(Dispatchers.Default) {
                    processChartData(
                        streakData,
                        selectedFilter
                    )
                }
                val heatmapData =
                    withContext(Dispatchers.Default) { processHeatmapData(relapseEvents) }

                _uiState.update {
                    it.copy(
                        relapseEvents = relapseEvents,
                        streakData = streakData,
                        chartData = chartData,
                        heatmapData = heatmapData,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onFilterSelected(filter: DateFilter) {
        val streakData = _uiState.value.streakData
        val chartData = processChartData(streakData, filter)

        _uiState.update {
            it.copy(
                selectedFilter = filter,
                chartData = chartData
            )
        }
    }

    private fun calculateStreakData(events: List<RelapseEvent>): List<StreakData> {
        if (events.isEmpty()) return emptyList()

        val sortedEvents = events.sortedBy { it.occurredAt }
        val streakList = mutableListOf<StreakData>()

        sortedEvents.forEachIndexed { index, event ->
            try {
                val streakData = if (index == 0 && event.streak == 0) {
                    StreakData(
                        relapseDate = event.occurredAt,
                        streakDays = calculateCurrentStreak(event.occurredAt)
                    )
                } else {
                    StreakData(
                        relapseDate = event.occurredAt,
                        streakDays = event.streak,
                    )
                }
                streakList.add(streakData)
            } catch (_: Exception) {
                // Skip invalid dates
            }
        }

        return streakList
    }

    @OptIn(ExperimentalTime::class)
    private fun processChartData(
        streakData: List<StreakData>,
        filter: DateFilter
    ): List<ChartDataPoint> {
        val filtered = if (filter.days != null) {
            val today = kotlin.time.Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
            val cutoff = today.minus(DatePeriod(days = filter.days))
            streakData.filter { streak ->
                try {
                    val relapseDate = kotlinx.datetime.Instant.parse(streak.relapseDate)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    relapseDate >= cutoff
                } catch (_: Exception) {
                    true
                }
            }
        } else {
            streakData
        }

        return filtered.map { streak ->
            ChartDataPoint(
                y = streak.streakDays.toFloat(),
                date = streak.relapseDate.formatDate(dateFormat2)
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun processHeatmapData(events: List<RelapseEvent>): List<HeatmapDay> {
        val today = kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = today.minus(DatePeriod(days = 364))

        val relapseDates = mutableSetOf<LocalDate>()
        for (event in events) {
            try {
                val date = kotlinx.datetime.Instant.parse(event.occurredAt)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                relapseDates.add(date)
            } catch (_: Exception) {
                // Skip invalid dates
            }
        }

        val heatmapDays = mutableListOf<HeatmapDay>()
        var currentStreak = 0
        var date = startDate

        while (date <= today) {
            val isRelapse = date in relapseDates
            if (isRelapse) {
                currentStreak = 0
            }
            heatmapDays.add(
                HeatmapDay(
                    date = date,
                    streakDays = currentStreak,
                    isRelapse = isRelapse
                )
            )
            if (!isRelapse) {
                currentStreak++
            }
            date = date.plus(DatePeriod(days = 1))
        }

        return heatmapDays
    }
}
