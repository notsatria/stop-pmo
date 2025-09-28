package dev.notsatria.stop_pmo.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.navigation.NavItem
import dev.notsatria.stop_pmo.navigation.Screen
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    navController: NavHostController
) {
    val theme = LocalTheme.current
    val navItems = listOf(
        NavItem(
            icon = R.drawable.ic_dashboard,
            label = "Dashboard",
            screen = Screen.Dashboard
        ),
        NavItem(
            icon = R.drawable.ic_history,
            label = "History",
            screen = Screen.History
        ),
        NavItem(
            icon = R.drawable.ic_settings,
            label = "Settings",
            screen = Screen.Settings
        ),
    )
    Column(modifier.fillMaxWidth()) {
        HorizontalDivider(Modifier.fillMaxWidth(), color = theme.divider)
        NavigationBar(
            Modifier,
            windowInsets = NavigationBarDefaults.windowInsets,
            containerColor = theme.surface,
            tonalElevation = NavigationBarDefaults.Elevation + 4.dp,
        ) {
            navItems.map { item ->
                NavigationBarItem(
                    selected = currentRoute == item.screen::class.qualifiedName,
                    onClick = {
                        navController.navigate(item.screen) {
                            popUpTo(navController.graph.id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            painterResource(item.icon),
                            contentDescription = item.label,
                            tint = if (currentRoute == item.screen::class.qualifiedName) theme.iconPrimary else theme.iconDisabled
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            color = if (currentRoute == item.screen::class.qualifiedName) theme.iconPrimary else theme.iconDisabled
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = theme.iconPrimary,
                        unselectedIconColor = theme.iconDisabled,
                        selectedTextColor = theme.iconPrimary,
                        unselectedTextColor = theme.iconDisabled,
                        indicatorColor = theme.iconPrimary.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    Surface(Modifier.fillMaxSize()) {
        Column {
            Spacer(Modifier.height(50.dp))
            Box(Modifier.weight(1f))
            HorizontalDivider()
            BottomNavBar(
                currentRoute = Screen.Dashboard::class.qualifiedName,
                navController = rememberNavController()
            )
        }
    }
}