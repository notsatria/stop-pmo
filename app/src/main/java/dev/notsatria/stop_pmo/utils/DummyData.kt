package dev.notsatria.stop_pmo.utils

import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import dev.notsatria.stop_pmo.ui.components.CalendarInput

object DummyData {
    fun generateCalendarInputList(): List<CalendarInput> = (1..31).map {
        CalendarInput(
            day = it,
            relapseList = listOf(
                RelapseEvent(
                    id = it,
                    occurredAt = "2023-09-${if (it < 10) "0$it" else it}T12:00:00Z",
                    note = if (it % 2 == 0) "Relapsed on day $it" else null
                )
            )
        )
    }
}