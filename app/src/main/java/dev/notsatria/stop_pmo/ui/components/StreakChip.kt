package dev.notsatria.stop_pmo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@Composable
fun StreakChip(modifier: Modifier = Modifier, streakCount: Int) {
    val theme = LocalTheme.current
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(theme.streakBackground)
    ) {
        Row(
            Modifier
                .padding(horizontal = 12.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(R.drawable.ic_fire_filled), null, tint = theme.streakForeground)
            Spacer(Modifier.width(4.dp))
            Text(
                "$streakCount Days",
                style = TextStyle(fontWeight = FontWeight.Medium, color = theme.streakForeground)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        StreakChip(streakCount = 10)
    }
}