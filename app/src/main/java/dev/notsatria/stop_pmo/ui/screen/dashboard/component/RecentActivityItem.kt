package dev.notsatria.stop_pmo.ui.screen.dashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@Composable
fun RecentActivityItem(modifier: Modifier = Modifier) {
    val theme = LocalTheme.current
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .background(color = theme.buttonPrimary.copy(alpha = 0.2f))
        ) {
            Icon(painterResource(R.drawable.ic_calendar), null, tint = theme.buttonPrimary, modifier = Modifier.padding(12.dp))
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                "Relapse Logged",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = theme.textPrimary
                )
            )
            Text(
                "2 days ago",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = theme.textSecondary
                )
            )
        }
    }
}