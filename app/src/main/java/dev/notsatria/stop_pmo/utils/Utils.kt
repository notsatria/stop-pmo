package dev.notsatria.stop_pmo.utils

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