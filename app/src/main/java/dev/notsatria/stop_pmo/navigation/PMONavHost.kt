package dev.notsatria.stop_pmo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.notsatria.stop_pmo.ui.screen.dashboard.DashboardRoute
import dev.notsatria.stop_pmo.ui.screen.history.HistoryRoute

@Composable
fun PMONavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Dashboard
    ) {
        graph(navController)
    }
}

private fun NavGraphBuilder.graph(navController: NavController) {
    composable<Screen.Dashboard> {
        DashboardRoute()
    }
    composable<Screen.History> {
        HistoryRoute()
    }
}