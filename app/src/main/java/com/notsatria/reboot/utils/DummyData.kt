package com.notsatria.reboot.utils

import com.notsatria.reboot.domain.model.RelapseEvent
import com.notsatria.reboot.ui.components.CalendarInput
import com.notsatria.reboot.ui.screen.history.RelapseHistoryItem

object DummyData {
    fun generateCalendarInputList(): List<CalendarInput> = (1..31).map {
        CalendarInput(
            day = it,
            relapseList = listOf(
                RelapseEvent(
                    id = it,
                    occurredAt = "2023-09-${if (it < 10) "0$it" else it}T12:00:00Z",
                    note = if (it % 2 == 0) "Relapsed on day $it" else null,
                    streak = it * 3
                )
            )
        )
    }

    fun generateRecentRelapses(): List<RelapseEvent> {
        return List(10) { index ->
            RelapseEvent(
                id = index + 1,
                occurredAt = "2023-09-${if (index + 1 < 10) "0${index + 1}" else index + 1}T12:00:00Z",
                note = if (index % 2 == 0) "Relapsed on day ${index + 1}" else null,
                streak = 10 - index
            )
        }
    }

    fun generateRelapseHistoryItems(): List<RelapseHistoryItem> {
        return List(10) { index ->
            RelapseHistoryItem(
                id = index + 1,
                occurredAt = "2023-09-${if (index + 1 < 10) "0${index + 1}" else index + 1}T12:00:00Z",
                note = if (index % 2 == 0) "Relapsed on day ${index + 1}" else null,
                streak = 10 - index
            )
        }
    }
}