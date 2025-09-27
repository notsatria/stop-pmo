package dev.notsatria.stop_pmo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.notsatria.stop_pmo.ui.theme.LocalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterTopBar(
    modifier: Modifier = Modifier,
    title: String,
    isStreakVisible: Boolean = false,
    streakCount: Int = 0
) {
    val theme = LocalTheme.current
    Box(
        modifier
            .fillMaxWidth()
            .height(TopAppBarDefaults.TopAppBarExpandedHeight)
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .background(theme.surface)
    ) {
        if (isStreakVisible) StreakChip(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
            streakCount = streakCount
        )
        Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                title,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = theme.textPrimary
                )
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MaterialTheme {
        CenterTopBar(
            Modifier, "Dashboard", isStreakVisible = true
        )
    }
}