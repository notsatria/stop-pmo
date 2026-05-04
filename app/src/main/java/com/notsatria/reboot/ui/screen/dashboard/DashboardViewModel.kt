package com.notsatria.reboot.ui.screen.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.reboot.data.preference.SettingsDataStore
import com.notsatria.reboot.domain.model.RelapseEvent
import com.notsatria.reboot.domain.repository.RelapseRepository
import com.notsatria.reboot.utils.getCurrentStreak
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber.Forest.d
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

enum class RelapseDialogStep {
    CONFIRMATION,
    DATE_TIME,
    REASON_INPUT
}

@OptIn(ExperimentalTime::class)
class DashboardViewModel(val repository: RelapseRepository, val settingDataStore: SettingsDataStore) : ViewModel() {
    private var _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    val use24HourFormat = settingDataStore.timeFormat24HFlow

    var showConfirmationDialog: Boolean by mutableStateOf(false)
        private set

    var currentDialogStep: RelapseDialogStep by mutableStateOf(RelapseDialogStep.CONFIRMATION)
        private set

    var relapseReason: String by mutableStateOf("")
        private set

    @OptIn(ExperimentalTime::class)
    var selectedRelapseTime: Instant by mutableStateOf(Clock.System.now())
        private set

    @OptIn(ExperimentalTime::class)
    fun showConfirmationDialog() {
        showConfirmationDialog = true
        currentDialogStep = RelapseDialogStep.CONFIRMATION
        relapseReason = ""
        selectedRelapseTime = Clock.System.now()
    }

    @OptIn(ExperimentalTime::class)
    fun dismissDialog() {
        showConfirmationDialog = false
        currentDialogStep = RelapseDialogStep.CONFIRMATION
        relapseReason = ""
        selectedRelapseTime = Clock.System.now()
    }

    fun moveToReasonStep() {
        currentDialogStep = RelapseDialogStep.REASON_INPUT
    }

    fun moveToDateTimeStep() {
        currentDialogStep = RelapseDialogStep.DATE_TIME
    }

    fun moveBackToConfirmation() {
        currentDialogStep = RelapseDialogStep.CONFIRMATION
    }

    fun moveBackToDateTime() {
        currentDialogStep = RelapseDialogStep.DATE_TIME
    }

    fun updateRelapseReason(reason: String) {
        relapseReason = reason
    }

    @OptIn(ExperimentalTime::class)
    fun updateSelectedRelapseTime(instant: Instant) {
        selectedRelapseTime = instant
    }

    @OptIn(ExperimentalTime::class)
    fun submitRelapse() {
        viewModelScope.launch {
            val occurredAt = selectedRelapseTime
            d("Logging relapse at $occurredAt with reason: $relapseReason")
            withContext(Dispatchers.IO) {
                val lastRelapseTime = repository.lastRelapseTimeFlow().first()
                val streak = if (lastRelapseTime != null) {
                    (occurredAt - lastRelapseTime).inWholeDays.toInt().coerceAtLeast(0)
                } else {
                    0
                }
                repository.logRelapse(
                    occurredAt.toString(),
                    streak = streak,
                    note = relapseReason.ifBlank { null }
                )
            }
            dismissDialog()
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