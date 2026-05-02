package dev.notsatria.stop_pmo.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.notsatria.stop_pmo.data.preference.SettingsDataStore
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    fun completeOnboarding() {
        viewModelScope.launch {
            settingsDataStore.setOnboardingCompleted()
        }
    }
}
