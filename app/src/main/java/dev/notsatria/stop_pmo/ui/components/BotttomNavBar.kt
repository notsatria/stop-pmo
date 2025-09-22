package dev.notsatria.stop_pmo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.navigation.NavItem
import dev.notsatria.stop_pmo.navigation.Screen
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@Composable
fun BottomNavBar(modifier: Modifier = Modifier, currentRoute: String?) {
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
    )
    Surface(
        modifier
            .fillMaxWidth()
            .height(80.dp),
        color = theme.surface,
    ) {
        Box(Modifier) {
            HorizontalDivider(Modifier.fillMaxWidth(), color = theme.iconDisabled)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                navItems.map { item ->
                    BottomNavItem(
                        item = item,
                        isSelected = currentRoute == item.screen::class.qualifiedName
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(modifier: Modifier = Modifier, item: NavItem, isSelected: Boolean) {
    val theme = LocalTheme.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painterResource(item.icon),
            contentDescription = item.label,
            modifier = modifier,
            tint = if (isSelected) theme.iconPrimary else theme.iconDisabled
        )
        Spacer(Modifier.height(8.dp))
        Text(item.label, color = if (isSelected) theme.iconPrimary else theme.iconDisabled)
    }
}