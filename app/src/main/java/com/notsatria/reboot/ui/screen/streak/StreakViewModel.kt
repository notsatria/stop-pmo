package com.notsatria.reboot.ui.screen.streak

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class StreakViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val streakCount: Int = savedStateHandle.get<Int>("streakCount") ?: 0
}