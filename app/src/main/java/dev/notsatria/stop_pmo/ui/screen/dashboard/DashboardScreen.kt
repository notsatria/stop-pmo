package dev.notsatria.stop_pmo.ui.screen.dashboard

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.components.EmptyStateView
import dev.notsatria.stop_pmo.ui.components.HistoryItem
import dev.notsatria.stop_pmo.ui.components.HistoryItemType
import dev.notsatria.stop_pmo.ui.theme.CustomTheme
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.DummyData
import dev.notsatria.stop_pmo.utils.getBottomNavHeight
import dev.notsatria.stop_pmo.utils.toHHmmss
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration

@Composable
fun DashboardRoute(modifier: Modifier = Modifier, viewModel: DashboardViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardScreen(
        modifier = modifier,
        onRelapseClick = {
            viewModel.relapse()
        },
        uiState = uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onRelapseClick: () -> Unit = {},
    uiState: DashboardState = DashboardState(),
    theme: CustomTheme = LocalTheme.current
) {
    Scaffold(modifier, topBar = {
        CenterTopBar(
            title = "Dashboard",
            streakCount = uiState.currentStreak,
            isStreakVisible = true
        )
    }, containerColor = theme.surface) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            DayCounter(duration = uiState.elapsedTime)
            Spacer(Modifier.height(32.dp))
            Button(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(containerColor = theme.buttonPrimary),
                onClick = onRelapseClick
            ) {
                Text(
                    "Log Relapse",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Recent Activity",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textPrimary
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 20.dp)
            )
            Spacer(Modifier.height(20.dp))
            if (uiState.recentRelapses.isEmpty()) {
                EmptyStateView()
            } else {
                repeat(uiState.recentRelapses.size) {
                    HistoryItem(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp),
                        occurredAt = uiState.recentRelapses[it].occurredAt,
                        type = HistoryItemType.RECENT,
                    )
                }
            }
            Spacer(Modifier.height(getBottomNavHeight() + 60.dp))
        }
    }
}

@Composable
private fun DayCounter(modifier: Modifier = Modifier, duration: Duration) {
    val theme = LocalTheme.current
    Box(
        modifier = modifier
            .size(300.dp)
            .clip(CircleShape)
            .border(10.dp, theme.buttonPrimary, CircleShape)
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                duration.inWholeDays.toString(),
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
            )
            Text(
                "Days",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textSecondary
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                duration.toHHmmss(),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = theme.textSecondary
                )
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDark() {
    MaterialTheme {
        DashboardScreen(uiState = DashboardState(recentRelapses = DummyData.generateRecentRelapses()))
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    MaterialTheme {
        DashboardScreen()
    }
}