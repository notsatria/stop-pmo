package dev.notsatria.stop_pmo.navigation

import android.R.attr.bottom
import android.R.attr.x
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import dev.notsatria.stop_pmo.ui.screen.analytics.AnalyticsRoute
import dev.notsatria.stop_pmo.ui.screen.dashboard.DashboardRoute
import dev.notsatria.stop_pmo.ui.screen.history.HistoryRoute
import dev.notsatria.stop_pmo.ui.screen.settings.SettingRoute
import dev.notsatria.stop_pmo.ui.screen.streak.StreakRoute

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

    composable<Screen.Analytics>(enterTransition = {
        fadeIn(animationSpec = tween(300, easing = LinearEasing))
    }) {
        AnalyticsRoute()
    }

    composable<Screen.Settings>(enterTransition = {
        fadeIn(animationSpec = tween(300, easing = LinearEasing))
    }) {
        SettingRoute(navigateToStreakScreen = {
            navController.navigate(Screen.Streak(streakCount = 42))
        })
    }

    composable<Screen.Streak>(enterTransition = {
        slideIn(
            initialOffset = { fullSize -> IntOffset(0, fullSize.height) },
            animationSpec = tween(300, easing = LinearEasing)
        )
    }, exitTransition = {
        slideOut(
            targetOffset = { fullSize -> IntOffset(0, fullSize.height) },
            animationSpec = tween(300, easing = LinearEasing)
        )
    }) {
        StreakRoute(
            navigateToDashboard = {
                navController.popBackStack()
            },
        )
    }
}