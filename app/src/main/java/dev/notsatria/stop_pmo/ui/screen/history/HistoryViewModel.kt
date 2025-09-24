package dev.notsatria.stop_pmo.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.notsatria.stop_pmo.di.repositoryModule
import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import dev.notsatria.stop_pmo.domain.repository.RelapseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val relapseHistory: List<RelapseEvent> = emptyList(),
    val pageState: PageState = PageState(),
    val error: Throwable? = null,
    val isLoading: Boolean = false
)

data class PageState(
    val pageSize: Int = 15,
    val offset: Int = 0,
    val endReached: Boolean = false,
)

class HistoryViewModel(private val repository: RelapseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        val uiState = _uiState.value
        if (uiState.isLoading || uiState.pageState.endReached) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.getRelapseHistory(
                    count = uiState.pageState.pageSize,
                    offset = uiState.pageState.offset
                )
            }.onSuccess { newData ->
                val endReached = newData.size < uiState.pageState.pageSize
                _uiState.update {
                    it.copy(
                        relapseHistory = it.relapseHistory + newData,
                        pageState = it.pageState.copy(
                            offset = it.pageState.offset + newData.size,
                            endReached = endReached
                        ),
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error) }
            }
        }
    }

}