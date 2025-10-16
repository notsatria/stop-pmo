package dev.notsatria.stop_pmo.ui.screen.analytics

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.theme.CustomTheme
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.DummyData
import dev.notsatria.stop_pmo.utils.formatDateOnly
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import org.koin.androidx.compose.koinViewModel

@Composable
fun AnalyticsRoute(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AnalyticsScreen(
        uiState = uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    uiState: AnalyticsState = AnalyticsState(),
    theme: CustomTheme = LocalTheme.current
) {
    Scaffold(
        topBar = {
            CenterTopBar(title = "Analytics")
        },
        containerColor = theme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.chartData.isEmpty()) {
                EmptyStateMessage()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    StreakSummaryCard(streakData = uiState.streakData)
                    RelapseChart(
                        chartData = uiState.chartData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyStateMessage() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "No Data Available",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Start tracking your progress to see analytics",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun RelapseChart(
    modifier: Modifier = Modifier,
    chartData: List<ChartDataPoint>,
) {
    val theme = LocalTheme.current
    Card(
        modifier = modifier,
        border = BorderStroke(width = 2.dp, brush = SolidColor(theme.divider)),
        colors = CardDefaults.cardColors(containerColor = theme.cardContainer)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (chartData.isNotEmpty()) {
                LineChart(
                    modifier = Modifier
                        .fillMaxSize(),
                    data = listOf(
                        Line(
                            label = "Relapse Events",
                            values = chartData.map { it.y.toDouble() },
                            color = SolidColor(theme.buttonPrimary),
                            firstGradientFillColor = theme.buttonPrimary.copy(alpha = 0.3f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(
                                2000,
                                easing = EaseInOutCubic
                            ),
                            gradientAnimationDelay = 500,
                            drawStyle = DrawStyle.Stroke(width = 3.dp),
                            curvedEdges = true,
                            dotProperties = DotProperties(
                                enabled = true,
                                color = SolidColor(MaterialTheme.colorScheme.surface),
                                strokeWidth = 2.dp,
                                radius = 6.dp,
                                strokeColor = SolidColor(theme.buttonPrimary)
                            )
                        ),
                    ),
                    indicatorProperties = HorizontalIndicatorProperties(
                        enabled = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = theme.textPrimary)
                    ),
                    labelHelperProperties = LabelHelperProperties(
                        enabled = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = theme.textPrimary),
                    ),
                    labelProperties = LabelProperties(
                        enabled = true,
                        textStyle = MaterialTheme.typography.labelSmall.copy(color = theme.textPrimary),
                        labels = chartData.map { it.date },
                    ),
                    animationMode = AnimationMode.Together(
                        delayBuilder = { it * 300L })
                )
            }
        }
    }
}

@Composable
private fun StreakSummaryCard(streakData: List<StreakData>) {
    val theme = LocalTheme.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(width = 2.dp, brush = SolidColor(theme.divider)),
        colors = CardDefaults.cardColors(
            containerColor = theme.cardContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Streak Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
            )

            if (streakData.isEmpty()) {
                Text(
                    text = "No streak data available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = theme.textPrimary
                )
            } else {
                val averageStreak = streakData.drop(1).map { it.streakDays }.average()
                val longestStreak = streakData.maxOfOrNull { it.streakDays } ?: 0
                val totalRelapses = streakData.size

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Relapses",
                            style = MaterialTheme.typography.labelMedium,
                            color = theme.textSecondary
                        )
                        Text(
                            text = totalRelapses.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary
                        )
                    }

                    Column {
                        Text(
                            text = "Longest Streak",
                            style = MaterialTheme.typography.labelMedium,
                            color = theme.textSecondary
                        )
                        Text(
                            text = "$longestStreak days",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = theme.streakForeground
                        )
                    }

                    if (averageStreak.isFinite() && !averageStreak.isNaN()) {
                        Column {
                            Text(
                                text = "Average Streak",
                                style = MaterialTheme.typography.labelMedium,
                                color = theme.textSecondary
                            )
                            Text(
                                text = "${averageStreak.toInt()} days",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = theme.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AnalyticsScreenPreview() {
    val streakData: List<StreakData> = remember {
        DummyData.generateRecentRelapses().map {
            StreakData(it.occurredAt.formatDateOnly(), it.streak)
        }
    }
    val chartData = remember {
        streakData.map {
            ChartDataPoint(
                y = it.streakDays.toFloat(),
                date = it.relapseDate
            )
        }
    }
    MaterialTheme {
        AnalyticsScreen(
            uiState = AnalyticsState(
                isLoading = false,
                chartData = chartData,
                streakData = streakData
            )
        )
    }
}
