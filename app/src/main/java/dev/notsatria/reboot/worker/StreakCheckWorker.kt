package dev.notsatria.stop_pmo.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.orhanobut.logger.Logger
import com.orhanobut.logger.Logger.t
import dev.notsatria.stop_pmo.data.preference.SettingsDataStore
import dev.notsatria.stop_pmo.domain.repository.RelapseRepository
import dev.notsatria.stop_pmo.utils.getCurrentStreak
import dev.notsatria.stop_pmo.utils.showStreakNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

const val INPUT_LAST_RELAPSE_EPOCH = "last_relapse_epoch"

class StreakCheckWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {
    @OptIn(ExperimentalTime::class)
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Logger.d("StreakCheckWorker started")
                val inputEpoch = inputData.getLong(INPUT_LAST_RELAPSE_EPOCH, -1L).also {
                    Logger.d("Input epoch: $it")
                }
                val relapseRepository: RelapseRepository = get<RelapseRepository>()
                val lastRelapse = relapseRepository.lastRelapse()
                val lastRelapseInstant = if (inputEpoch > 0L) {
                    Instant.fromEpochSeconds(inputEpoch)
                } else {
                    lastRelapse?.occurredAt?.let { Instant.parse(it) }
                }
                val now = Clock.System.now()
                val streak = getCurrentStreak(
                    lastRelapseInstant,
                    now
                ).also {
                    Logger.d("Current streak: $it")
                }

                if (streak > 0 && streak % 7 == 0) {
                    val settingsDataStore = get<SettingsDataStore>()
                    val lastNotifiedStreak = settingsDataStore.lastNotifiedStreakFlow
                        .first()
                    val notificationsEnabled = settingsDataStore.notificationEnabledFlow
                        .first()
                    if (notificationsEnabled && streak != lastNotifiedStreak) {
                        showStreakNotification(
                            applicationContext,
                            streak
                        )
                        settingsDataStore.setLastNotifiedStreak(streak)
                    }
                }
                Result.success()
            } catch (t: Throwable) {
                t.printStackTrace()
                Result.retry()
            }
        }
    }
}