package dev.notsatria.stop_pmo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.notsatria.stop_pmo.data.preference.SettingsDataStore
import dev.notsatria.stop_pmo.navigation.PMONavHost
import dev.notsatria.stop_pmo.navigation.Screen
import dev.notsatria.stop_pmo.ui.components.BottomNavBar
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.ui.theme.darkThemeColors
import dev.notsatria.stop_pmo.ui.theme.lightThemeColors
import dev.notsatria.stop_pmo.utils.UiMode
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StopPmoApp()
        }
    }
}

@Composable
private fun StopPmoApp(
    navController: NavHostController = rememberNavController(),
) {
    val settingsDataStore: SettingsDataStore = koinInject()
    val uiMode by settingsDataStore.uiModeFlow.collectAsState(initial = UiMode.DARK)
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