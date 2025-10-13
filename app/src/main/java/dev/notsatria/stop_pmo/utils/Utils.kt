package dev.notsatria.stop_pmo.utils

import android.Manifest
import android.R.attr.description
import android.R.id.message
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.notsatria.stop_pmo.MainActivity
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.utils.StreakNotification.CHANNEL_ID
import dev.notsatria.stop_pmo.utils.StreakNotification.NOTIFICATION_ID
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

object UiMode {
    const val SYSTEM = 0
    const val LIGHT = 1
    const val DARK = 2
}

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                StreakNotification.CHANNEL_NAME,
                importance
            )
                .apply {
                    description = StreakNotification.CHANNEL_DESCRIPTION
                }
        val notificationManager: android.app.NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showStreakNotification(
    context: Context,
    streak: Int,
) {
    createNotificationChannel(context)

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingFlags = (PendingIntent.FLAG_UPDATE_CURRENT
            or PendingIntent.FLAG_IMMUTABLE)

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        pendingFlags
    )

    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_fire_filled)
            .setContentTitle("\uD83C\uDF89 ${streak}-day streak!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Amazing — you reached $streak days! Keep the momentum.")
            )
            .setContentIntent(pendingIntent)
            .setSound(soundUri)
            .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notify(NOTIFICATION_ID, builder.build())
    }
}

object StreakNotification {
    const val CHANNEL_ID = "streak_channel"
    const val CHANNEL_NAME = "Streak Notifications"
    const val CHANNEL_DESCRIPTION = "Notifications to encourage maintaining streaks"
    const val NOTIFICATION_ID = 1001
}