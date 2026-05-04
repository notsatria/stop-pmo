package com.notsatria.reboot.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.reboot.data.preference.SettingsDataStore
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
