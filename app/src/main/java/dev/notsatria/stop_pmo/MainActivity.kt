package dev.notsatria.stop_pmo

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.orhanobut.logger.Logger
import dev.notsatria.stop_pmo.data.preference.SettingsDataStore
import dev.notsatria.stop_pmo.navigation.PMONavHost
import dev.notsatria.stop_pmo.navigation.Screen
import dev.notsatria.stop_pmo.ui.components.BottomNavBar
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.ui.theme.darkThemeColors
import dev.notsatria.stop_pmo.ui.theme.lightThemeColors
import dev.notsatria.stop_pmo.utils.DebugEnqueueReceiver
import dev.notsatria.stop_pmo.utils.UiMode
import dev.notsatria.stop_pmo.worker.WorkScheduler
import dev.notsatria.stop_pmo.worker.scheduleDailyStreakCheck
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    private val broadcast = DebugEnqueueReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intentFilter = IntentFilter("dev.notsatria.stop_pmo.action.ENQUEUE_STREAK_CHECK")
        ContextCompat.registerReceiver(
            this@MainActivity,
            broadcast,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        enableEdgeToEdge()
        setContent {
            StopPmoApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
       unregisterReceiver(broadcast)
    }
}

@Composable
private fun StopPmoApp(
    navController: NavHostController = rememberNavController(),
    context: android.content.Context = LocalContext.current
) {
    val settingsDataStore: SettingsDataStore = koinInject()
    val uiMode by settingsDataStore.uiModeFlow.collectAsState(initial = UiMode.DARK)
    val isStreakNotificationEnabled by settingsDataStore.notificationEnabledFlow.collectAsState(
        initial = false
    )

    Logger.d("Streak notification is $isStreakNotificationEnabled, scheduling daily check")
    if (isStreakNotificationEnabled) {
        scheduleDailyStreakCheck(context)
    } else {
        WorkScheduler.cancelStreakCheckWork(context)
    }

    val themeColors = when (uiMode) {
        UiMode.LIGHT -> lightThemeColors
        UiMode.DARK -> darkThemeColors
        else -> if (isSystemInDarkTheme()) darkThemeColors else lightThemeColors
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomBarVisibleRoutes = listOf(
        Screen.Dashboard::class.qualifiedName,
        Screen.History::class.qualifiedName,
        Screen.Analytics::class.qualifiedName,
        Screen.Settings::class.qualifiedName
    )
    CompositionLocalProvider(LocalTheme provides themeColors) {
        MaterialTheme {
            Scaffold(bottomBar = {
                if (currentRoute in bottomBarVisibleRoutes) BottomNavBar(
                    currentRoute = currentRoute,
                    navController = navController
                )
            }) { _ ->
                PMONavHost(modifier = Modifier, navController = navController)
            }
        }
    }
}