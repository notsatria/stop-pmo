package dev.notsatria.stop_pmo.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.notsatria.stop_pmo.ui.screen.dashboard.DashboardRoute
import dev.notsatria.stop_pmo.ui.screen.history.HistoryRoute
import dev.notsatria.stop_pmo.ui.screen.settings.SettingRoute

@Composable
fun PMONavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Dashboard,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        graph(navController)
    }
}

private fun NavGraphBuilder.graph(navController: NavController) {
    composable<Screen.Dashboard>(enterTransition = {
        fadeIn(animationSpec = tween(300, easing = LinearEasing))
    }) {
        DashboardRoute()
    }
    composable<Screen.History>(enterTransition = {
        fadeIn(animationSpec = tween(300, easing = LinearEasing))
    }) {
        HistoryRoute(onNavigateToDashboard = {
            navController.navigate(Screen.Dashboard) {
                popUpTo(Screen.Dashboard) {
                    inclusive = true
                }
            }
        })
    }
    composable<Screen.Settings>(enterTransition = {
        fadeIn(animationSpec = tween(300, easing = LinearEasing))
    }) {
        SettingRoute()
    }
}