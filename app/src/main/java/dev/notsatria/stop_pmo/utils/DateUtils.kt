package dev.notsatria.stop_pmo.utils

import android.os.Build
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalQueries.zoneId
import java.util.Date
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

fun Duration.toHHmmss(): String {
    val hours = this.inWholeHours
    val minutes = this.inWholeMinutes % 60
    val seconds = this.inWholeSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}

@OptIn(ExperimentalTime::class)
fun String.toDayAgo(): String {
    try {
        val duration = Clock.System.now() - Instant.parse(this)
        val day = duration.inWholeDays
        return if (day == 1L) {
            "$day day ago"
        } else {
            "$day days ago"
        }
    } catch (_: Exception) {
        return "unknown"
    }
}