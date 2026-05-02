package dev.notsatria.stop_pmo.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.orhanobut.logger.Logger
import dev.notsatria.stop_pmo.worker.INPUT_LAST_RELAPSE_EPOCH
import dev.notsatria.stop_pmo.worker.StreakCheckWorker

class DebugEnqueueReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null && intent.action!!.equals("dev.notsatria.stop_pmo.action.ENQUEUE_STREAK_CHECK", ignoreCase = true)) {
            Logger.d("Received intent to enqueue StreakCheckWorker")
        } else {
            Logger.d("Received unknown intent: ${intent.action}")
            return
        }
        val epoch = intent.getLongExtra("epoch", -1L)
        val builder = OneTimeWorkRequestBuilder<StreakCheckWorker>()
        if (epoch > 0) {
            Logger.d("Enqueuing StreakCheckWorker with epoch: $epoch")
            builder.setInputData(workDataOf(INPUT_LAST_RELAPSE_EPOCH to epoch))
        }
        WorkManager.getInstance(context).enqueue(builder.build())
    }
}

// Add this to your DebugEnqueueReceiver or create a test method
fun testWorker(context: Context, epoch: Long = System.currentTimeMillis() / 1000) {
    val intent = Intent("dev.notsatria.stop_pmo.action.ENQUEUE_STREAK_CHECK").apply {
        putExtra("epoch", epoch)
    }
    context.sendBroadcast(intent)
}