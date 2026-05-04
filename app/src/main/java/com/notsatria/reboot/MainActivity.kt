package com.notsatria.reboot

import android.content.Intent
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orhanobut.logger.Logger
import com.notsatria.reboot.data.preference.SettingsDataStore
import com.notsatria.reboot.domain.repository.RelapseRepository
import com.notsatria.reboot.navigation.PMONavHost
import com.notsatria.reboot.navigation.Screen
import com.notsatria.reboot.ui.components.BottomNavBar
import com.notsatria.reboot.ui.theme.LocalTheme
import com.notsatria.reboot.ui.theme.darkThemeColors
import com.notsatria.reboot.ui.theme.lightThemeColors
import com.notsatria.reboot.utils.DebugEnqueueReceiver
import com.notsatria.reboot.utils.UiMode
import com.notsatria.reboot.worker.WorkScheduler
import com.notsatria.reboot.worker.scheduleDailyStreakCheck
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
            StopPmoApp(intent = intent)
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
    context: android.content.Context = LocalContext.current,
    intent: Intent
) {
    val settingsDataStore: SettingsDataStore = koinInject()
    val repository: RelapseRepository = koinInject()

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

    @OptIn(kotlin.time.ExperimentalTime::class)
    val startDestination by remember {
        combine(
            settingsDataStore.hasCompletedOnboarding,
            repository.lastRelapseTimeFlow().map { it != null }
        ) { completed, hasData ->
            if (completed || hasData) Screen.Dashboard else Screen.Onboarding
        }
    }.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        intent.getStringExtra("nav_target").let { target ->
            val streakCount = intent.getIntExtra("streak_count", 0)
            if (target == "streak") {
                navController.navigate(Screen.Streak(streakCount)) {
                    popUpTo(Screen.Dashboard) {
                        inclusive = false
                    }
                }
            }
        }
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
                startDestination?.let { destination ->
                    PMONavHost(
                        modifier = Modifier,
                        navController = navController,
                        startDestination = destination
                    )
                }
            }
        }
    }
}