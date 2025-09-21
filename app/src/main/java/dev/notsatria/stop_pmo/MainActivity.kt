package dev.notsatria.stop_pmo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import dev.notsatria.stop_pmo.ui.screen.dashboard.DashboardRoute
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.ui.theme.darkThemeColors
import dev.notsatria.stop_pmo.ui.theme.lightThemeColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Composable
private fun App(modifier: Modifier = Modifier) {
    val themeColors = if (isSystemInDarkTheme()) darkThemeColors else lightThemeColors
    CompositionLocalProvider(LocalTheme provides themeColors) {
        MaterialTheme {
            DashboardRoute()
        }
    }
}