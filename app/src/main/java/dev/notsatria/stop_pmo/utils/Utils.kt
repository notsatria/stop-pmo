package dev.notsatria.stop_pmo.utils

import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun getCurrentStreak(
    lastRelapse: Instant?,
    now: Instant
): Int {
    if (lastRelapse == null) return 0
    val daysBetween = (now - lastRelapse).inWholeDays
    return daysBetween.toInt()
}

@Composable
fun getBottomNavHeight(): Dp {
    val density = LocalDensity.current
    val windowInsets = NavigationBarDefaults.windowInsets
    return with(density) {
        windowInsets.getBottom(density).toDp()
    }
}