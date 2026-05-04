package com.notsatria.reboot.navigation

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
import androidx.navigation.toRoute
import com.notsatria.reboot.ui.screen.analytics.AnalyticsRoute
import com.notsatria.reboot.ui.screen.dashboard.DashboardRoute
import com.notsatria.reboot.ui.screen.history.HistoryRoute
import com.notsatria.reboot.ui.screen.onboarding.OnboardingRoute
import com.notsatria.reboot.ui.screen.settings.SettingRoute
import com.notsatria.reboot.ui.screen.streak.StreakRoute
import com.notsatria.reboot.ui.screen.webview.WebViewRoute

@Composable
fun PMONavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Screen = Screen.Dashboard
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        graph(navController)
    }
}

private fun NavGraphBuilder.graph(navController: NavController) {
    composable<Screen.Onboarding> {
        OnboardingRoute(
            onComplete = {
                navController.navigate(Screen.Dashboard) {
                    popUpTo(Screen.Onboarding) { inclusive = true }
                }
            }
        )
    }

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
        SettingRoute(
            navigateToStreakScreen = {
                navController.navigate(Screen.Streak(streakCount = 42))
            },
            navigateToWebView = { url, title ->
                navController.navigate(Screen.WebView(url = url, title = title))
            }
        )
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

    composable<Screen.WebView> { backStackEntry ->
        val screen = backStackEntry.toRoute<Screen.WebView>()
        WebViewRoute(
            url = screen.url,
            title = screen.title,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}