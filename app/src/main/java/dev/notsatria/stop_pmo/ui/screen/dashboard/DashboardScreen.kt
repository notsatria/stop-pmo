package dev.notsatria.stop_pmo.ui.screen.dashboard

import android.content.res.Configuration
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
import dev.notsatria.stop_pmo.domain.model.RelapseEvent
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.screen.dashboard.component.RecentActivityItem
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.toHHmmss
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Duration

@Composable
fun DashboardRoute(modifier: Modifier = Modifier, viewModel: DashboardViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardScreen(
        modifier = modifier,
        elapsedTime = uiState.elapsedTime,
        onRelapseClick = {
            viewModel.relapse()
        },
        recentRelapses = uiState.recentRelapses
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    elapsedTime: Duration = Duration.ZERO,
    onRelapseClick: () -> Unit = {},
    recentRelapses: List<RelapseEvent> = emptyList()
) {
    val theme = LocalTheme.current
    Scaffold(modifier, topBar = {
        CenterTopBar(title = "Dashboard")
    }, containerColor = theme.surface) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            DayCounter(duration = elapsedTime)
            Spacer(Modifier.height(32.dp))
            Button(
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(containerColor = theme.buttonPrimary),
                onClick = onRelapseClick
            ) {
                Text("Relapse", modifier = Modifier.padding(8.dp))
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
            repeat(recentRelapses.size) {
                RecentActivityItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp),
                    occurredAt = recentRelapses[it].occurredAt
                )
            }
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
        DashboardScreen()
    }
}