package dev.notsatria.stop_pmo.worker

import android.content.Context
import android.os.Build
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.concurrent.TimeUnit

fun scheduleDailyStreakCheck(context: Context, hourOfDay: Int = 9) {
    // Calculate initial delay to align the work to the specified hour
    val initialDelayMillis = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val now = ZonedDateTime.now()
        var next = now
            .withHour(hourOfDay)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        Duration.between(now, next).toMillis()
    } else {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now) add(Calendar.DAY_OF_MONTH, 1)
        }
        calendar.timeInMillis
    }

    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(false)
        .build()

    val work = PeriodicWorkRequestBuilder<StreakCheckWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "streak_check_work",
        ExistingPeriodicWorkPolicy.KEEP,
        work
    )
}

object WorkScheduler {
    const val STREAK_CHECK_WORK = "streak_check_work"

    fun cancelStreakCheckWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(STREAK_CHECK_WORK)
    }
}