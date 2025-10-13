package dev.notsatria.stop_pmo.utils

import java.text.SimpleDateFormat
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
        return when {
            day < 1L -> {
                val hours = duration.inWholeHours
                if (hours < 1L) {
                    val minutes = duration.inWholeMinutes
                    if (minutes < 1L) {
                        "just now"
                    } else if (minutes == 1L) {
                        "$minutes minute ago"
                    } else {
                        "$minutes minutes ago"
                    }
                } else if (hours == 1L) {
                    "$hours hour ago"
                } else {
                    "$hours hours ago"
                }
            }

            day == 1L -> {
                "$day day ago"
            }

            else -> {
                "$day days ago"
            }
        }
    } catch (_: Exception) {
        return "unknown"
    }
}

val defaultDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
val timeFormat24H = SimpleDateFormat("HH:mm", Locale.ENGLISH)
val timeFormat12H = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

@OptIn(ExperimentalTime::class)
fun String.formatDate(use24Hour: Boolean): String {
    try {
        val instant = Instant.parse(this)
        val date = Date(instant.toEpochMilliseconds())
        val result = buildString {
            append(defaultDateFormat.format(date))
            append(" ")
            append("at")
            append(" ")
            if (use24Hour) append(timeFormat24H.format(date))
            else append(timeFormat12H.format(date))
        }
        return result
    } catch (_: Exception) {
        return this
    }
}