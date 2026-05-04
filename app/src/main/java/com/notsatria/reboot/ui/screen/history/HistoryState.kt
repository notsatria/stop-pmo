package com.notsatria.reboot.ui.screen.history

data class HistoryUiState(
    val relapseHistory: List<RelapseHistoryItem> = emptyList(),
    val pageState: PageState = PageState(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val use24HourFormat: Boolean = false
)