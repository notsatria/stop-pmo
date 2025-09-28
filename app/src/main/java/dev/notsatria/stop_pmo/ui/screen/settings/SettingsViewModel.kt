package dev.notsatria.stop_pmo.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dev.notsatria.stop_pmo.data.preference.SettingsDataStore
import dev.notsatria.stop_pmo.utils.UiMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingState(
    val settings: List<Setting> = emptyList(),
)

class SettingsViewModel(private val settingsPref: SettingsDataStore) : ViewModel() {
    private val _uiState = MutableStateFlow(
        SettingState()
    )
    val uiState = _uiState.asStateFlow()

    init {
        initPreferences()
    }

    private fun initPreferences() {
        viewModelScope.launch {
            combine(
                settingsPref.uiModeFlow,
                settingsPref.notificationEnabledFlow,
                settingsPref.timeFormat24HFlow
            ) { uiMode, notificationsEnabled, timeFormat24H ->
                createSettingsList(
                    uiMode,
                    notificationsEnabled,
                    timeFormat24H
                )
            }.collect { settingList ->
                _uiState.update {
                    it.copy(settings = settingList.also {
                        Logger.d("Settings updated: $settingList")
                    })
                }
            }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsPref.setNotificationEnabled(enabled)
        }
    }

    fun toggleDarkMode(uiMode: Int) {
        viewModelScope.launch {
            settingsPref.setUiMode(uiMode)
        }
    }

    fun toggleTimeFormat(enabled: Boolean) {
        viewModelScope.launch {
            settingsPref.setTimeFormat24H(enabled)
        }
    }
}