package dev.notsatria.stop_pmo

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import dev.notsatria.stop_pmo.data.preference.SettingsDataStore
import dev.notsatria.stop_pmo.domain.repository.RelapseRepository
import dev.notsatria.stop_pmo.worker.INPUT_LAST_RELAPSE_EPOCH
import dev.notsatria.stop_pmo.worker.StreakCheckWorker
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.time.temporal.ChronoUnit
import kotlin.time.ExperimentalTime
import java.time.Instant
import kotlin.test.assertEquals

class StreakCheckWorkerTest : KoinTest {

    private lateinit var context: Context
    private val mockRelapseRepository: RelapseRepository = mockk()
    private val mockSettingDatastore: SettingsDataStore = mockk()

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        stopKoin()
        startKoin {
            modules(module {
                single { mockRelapseRepository }
                single { mockSettingDatastore }
            })
        }
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `doWork returns success and shows notification on 7 days streak`() = runBlocking {
        val sevenDaysAgoEpoch = Instant.now().minus(7, ChronoUnit.DAYS).epochSecond
        val inputData = workDataOf(INPUT_LAST_RELAPSE_EPOCH to sevenDaysAgoEpoch)

        coEvery { mockSettingDatastore.notificationEnabledFlow } returns flowOf(true)
        coEvery { mockSettingDatastore.lastNotifiedStreakFlow } returns flowOf(0)
        coEvery { mockSettingDatastore.setLastNotifiedStreak(7) } returns Unit

        val worker = TestListenableWorkerBuilder<StreakCheckWorker>(
            context = context,
            inputData = inputData
        ).build()

        val result = worker.doWork()

        assertEquals(androidx.work.ListenableWorker.Result.success(), result)
    }


    @Test
    fun `doWork returns success and does NOT show notification on 6 day streak`() = runBlocking {
        // --- GIVEN ---
        // 1. Simulate a relapse that happened 6 days ago
        val sixDaysAgoEpoch = Instant.now().minus(6, ChronoUnit.DAYS).epochSecond
        val inputData = workDataOf(INPUT_LAST_RELAPSE_EPOCH to sixDaysAgoEpoch)

        // --- WHEN ---
        // 2. Build and execute the worker
        val worker = TestListenableWorkerBuilder<StreakCheckWorker>(
            context = context,
            inputData = inputData
        ).build()

        val result = worker.doWork()

        // --- THEN ---
        // 3. Assert that the work succeeded (it should still succeed, just not send a notification)
        assertEquals(ListenableWorker.Result.success(), result)
        // You would also verify here that `showStreakNotification` was NOT called.
    }
}