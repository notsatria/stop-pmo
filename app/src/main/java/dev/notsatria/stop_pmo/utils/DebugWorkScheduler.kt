package dev.notsatria.stop_pmo.utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dev.notsatria.stop_pmo.worker.INPUT_LAST_RELAPSE_EPOCH
import dev.notsatria.stop_pmo.worker.StreakCheckWorker
import java.time.Instant
import java.time.temporal.ChronoUnit

object DebugWorkScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleImmediateStreakCheck(context: Context, day: Int) {
        val workManager = WorkManager.getInstance(context)
        val streakCheckRequest = OneTimeWorkRequestBuilder<StreakCheckWorker>()
            .setInputData(
                workDataOf(
                    INPUT_LAST_RELAPSE_EPOCH to Instant.now()
                        .minus(day.toLong(), ChronoUnit.DAYS).epochSecond
                )
            )
            .build()
        workManager.enqueue(streakCheckRequest)
    }
}