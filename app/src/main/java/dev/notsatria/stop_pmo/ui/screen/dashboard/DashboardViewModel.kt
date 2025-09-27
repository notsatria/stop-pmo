package dev.notsatria.stop_pmo.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import dev.notsatria.stop_pmo.domain.repository.RelapseRepository
import dev.notsatria.stop_pmo.utils.getCurrentStreak
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication.Companion.init
import timber.log.Timber.Forest.d
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class DashboardViewModel(val repository: RelapseRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    fun relapse() {
        viewModelScope.launch {
            val now = Clock.System.now()
            d("Logging relapse at $now")
            repository.logRelapse(now.toString(), null)
        }
    }

    private fun tickerFlow(periodMs: Long = 1000L): Flow<Long> = flow {
        while (currentCoroutineContext().isActive) {
            emit(System.currentTimeMillis())
            delay(periodMs)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val elapsedTime: Flow<Duration> =
        repository.lastRelapseTimeFlow().flatMapLatest { base: Instant? ->
            if (base == null) flowOf(Duration.ZERO)
            else tickerFlow().map {
                val now = Clock.System.now()
                getCurrentStreak(lastRelapse = base, now = now).let {
                    _uiState.update { state ->
                        state.copy(currentStreak = it)
                    } // Update current streak
                    now - base
                }
            }
        }

    private val recentRelapses: Flow<List<RelapseEvent>> = repository.recentRelapses(10)

    init {
        viewModelScope.launch {
            launch {
                elapsedTime.collect { elapsed ->
                    _uiState.update { it.copy(elapsedTime = elapsed) }
                }
            }
            launch {
                recentRelapses.collect { list ->
                    _uiState.update { it.copy(recentRelapses = list) }
                }
            }
        }
    }
}