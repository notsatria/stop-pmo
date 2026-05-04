package com.notsatria.reboot.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.reboot.data.preference.SettingsDataStore
import com.notsatria.reboot.domain.repository.RelapseRepository
import com.notsatria.reboot.utils.calculateCurrentStreak
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

data class PageState(
    val pageSize: Int = 15,
    val offset: Int = 0,
    val endReached: Boolean = false,
)

class HistoryViewModel(
    private val repository: RelapseRepository,
    private val settingPref: SettingsDataStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeRelapseHistory()
    }

    @OptIn(ExperimentalTime::class)
    private fun observeRelapseHistory() {
        viewModelScope.launch {
            repository.getRelapseHistory(
                count = Int.MAX_VALUE,
                offset = 0
            ).collect { allRelapses ->
                _uiState.update { currentState ->
                    val pageState = currentState.pageState
                    val itemsToShow = allRelapses.take(pageState.offset + pageState.pageSize)
                    val endReached = itemsToShow.size == allRelapses.size

                    val mappedItems = itemsToShow.mapIndexed { index, relapse ->
                        val displayStreak = if (index == 0 && relapse.streak == 0) {
                            calculateCurrentStreak(relapse.occurredAt)
                        } else {
                            relapse.streak
                        }
                        
                        RelapseHistoryItem(
                            id = relapse.id,
                            occurredAt = relapse.occurredAt,
                            streak = displayStreak,
                            note = relapse.note
                        )
                    }

                    currentState.copy(
                        relapseHistory = mappedItems,
                        pageState = pageState.copy(endReached = endReached),
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    fun loadNextPage() {
        val currentUiState = _uiState.value
        if (currentUiState.isLoading || currentUiState.pageState.endReached) return

        _uiState.update {
            it.copy(
                isLoading = true,
                pageState = it.pageState.copy(
                    offset = it.pageState.offset + it.pageState.pageSize
                )
            )
        }
    }

    fun loadUserSettings() {
        viewModelScope.launch {
            val use24HourFormat = settingPref.timeFormat24HFlow.first()
            _uiState.update {
                it.copy(use24HourFormat = use24HourFormat)
            }
        }
    }
}