package com.notsatria.reboot.ui.screen.analytics

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.notsatria.reboot.R
import com.notsatria.reboot.ui.components.CenterTopBar
import com.notsatria.reboot.ui.screen.analytics.AnalyticsState
import com.notsatria.reboot.ui.screen.analytics.AnalyticsViewModel
import com.notsatria.reboot.ui.screen.analytics.ChartDataPoint
import com.notsatria.reboot.ui.screen.analytics.DateFilter
import com.notsatria.reboot.ui.screen.analytics.HeatmapDay
import com.notsatria.reboot.ui.screen.analytics.StreakData
import com.notsatria.reboot.ui.theme.CustomTheme
import com.notsatria.reboot.ui.theme.LocalTheme
import com.notsatria.reboot.utils.DummyData
import com.notsatria.reboot.utils.dateFormat2
import com.notsatria.reboot.utils.formatDate
import com.notsatria.reboot.utils.formatDateOnly
import com.notsatria.reboot.utils.getBottomNavHeight
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import kotlinx.coroutines.delay
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val HeatmapGreen1 = Color(0xFFBBF7D0)
private val HeatmapGreen2 = Color(0xFF86EFAC)
private val HeatmapGreen3 = Color(0xFF4ADE80)
private val HeatmapGreen4 = Color(0xFF22C55E)
private val HeatmapGreen5 = Color(0xFF166534)
private val HeatmapRelapse = Color(0xFFDC2626)
private val HeatmapRelapseBorder = Color(0xFFFCA5A5)

private fun heatmapColor(streakDays: Int, isRelapse: Boolean, dividerColor: Color): Color {
    if (isRelapse) return HeatmapRelapse
    return when {
        streakDays == 0 -> dividerColor
        streakDays <= 7 -> HeatmapGreen1
        streakDays <= 14 -> HeatmapGreen2
        streakDays <= 30 -> HeatmapGreen3
        streakDays <= 60 -> HeatmapGreen4
        else -> HeatmapGreen5
    }
}

@Composable
fun AnalyticsRoute(
    modifier: Modifier = Modifier,
    viewModel: AnalyticsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AnalyticsScreen(
        uiState = uiState,
        onFilterSelected = viewModel::onFilterSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    uiState: AnalyticsState = AnalyticsState(),
    onFilterSelected: (DateFilter) -> Unit = {},
    theme: CustomTheme = LocalTheme.current
) {
    Scaffold(
        topBar = {
            CenterTopBar(title = "Analytics")
        },
        containerColor = theme.surface
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            if (uiState.isLoading) {
                item {
                    LoadingIndicator()
                }
            } else if (uiState.chartData.isEmpty() && uiState.heatmapData.isEmpty()) {
                item {
                    EmptyStateMessage()
                }
            } else {
                item {
                    StreakSummaryCard(streakData = uiState.streakData)
                }
                item { Spacer(Modifier.height(16.dp)) }
                item {
                    HeatmapCard(heatmapData = uiState.heatmapData)
                }
                item { Spacer(Modifier.height(16.dp)) }
                item {
                    DateFilterChips(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelected = onFilterSelected
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
                item {
                    RelapseChart(
                        chartData = uiState.chartData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )
                }
                item { Spacer(Modifier.height(getBottomNavHeight() + 60.dp)) }
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
    val theme = LocalTheme.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = theme.cardContainer,
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(color = theme.buttonPrimary.copy(alpha = 0.2f))
                ) {
                    Icon(
                        painterResource(R.drawable.ic_analytics_outline),
                        null,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center),
                        tint = theme.buttonPrimary
                    )
                }
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

@OptIn(ExperimentalTime::class)
@Composable
private fun HeatmapCard(heatmapData: List<HeatmapDay>) {
    val theme = LocalTheme.current
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    val monthLabels = remember(heatmapData) {
        if (heatmapData.isEmpty()) return@remember emptyList()
        val startDate = heatmapData.first().date
        val labels = mutableListOf<Pair<Int, String>>()
        var currentMonth = startDate.month
        for ((index, day) in heatmapData.withIndex()) {
            if (day.date.month != currentMonth) {
                val weekCol = index / 7
                labels.add(weekCol to day.date.month.name.take(3))
                currentMonth = day.date.month
            }
        }
        labels
    }

    val heatmapWidth = remember(heatmapData) {
        val weeks = (heatmapData.size + 6) / 7
        weeks * 16
    }

    val scrollState = rememberScrollState(initial = Int.MAX_VALUE)

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(width = 2.dp, brush = SolidColor(theme.divider)),
        colors = CardDefaults.cardColors(containerColor = theme.cardContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = theme.textPrimary
                )
                HeatmapLegend()
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Column(
                    modifier = Modifier.padding(end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    for (label in listOf("M", "", "W", "", "F", "", "")) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = theme.textSecondary,
                            fontSize = 8.sp,
                            modifier = Modifier.height(14.dp)
                        )
                    }
                }

                Column {
                    Box(
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .width(heatmapWidth.dp)
                            .padding(bottom = 4.dp)
                    ) {
                        for ((weekCol, monthName) in monthLabels) {
                            Text(
                                text = monthName,
                                style = MaterialTheme.typography.labelSmall,
                                color = theme.textSecondary,
                                fontSize = 9.sp,
                                modifier = Modifier.offset(x = (weekCol * 16).dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val weeks = heatmapData.chunked(7)
                        for (week in weeks) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                for (day in week) {
                                    HeatmapCell(
                                        day = day,
                                        dividerColor = theme.divider
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatmapCell(day: HeatmapDay, dividerColor: Color) {
    var showTooltip by remember { mutableStateOf(false) }
    val cellColor = heatmapColor(day.streakDays, day.isRelapse, dividerColor)

    Box {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(cellColor)
                .then(
                    if (day.isRelapse) {
                        Modifier.background(cellColor, RoundedCornerShape(2.dp))
                    } else {
                        Modifier
                    }
                )
                .clickable { showTooltip = !showTooltip }
        )

        if (showTooltip) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -40),
                onDismissRequest = { showTooltip = false },
                properties = PopupProperties(focusable = true)
            ) {
                LaunchedEffect(Unit) {
                    delay(2000)
                    showTooltip = false
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF333333),
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = day.date.toString(),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                        if (day.isRelapse) {
                            Text(
                                text = "Relapse",
                                color = HeatmapRelapseBorder,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "${day.streakDays} day streak",
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Surface(
    shape: RoundedCornerShape,
    color: Color,
    shadowElevation: Dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(shape)
            .background(color)
            .padding(shadowElevation)
    ) {
        content()
    }
}

@Composable
private fun HeatmapLegend() {
    val theme = LocalTheme.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Less",
            style = MaterialTheme.typography.labelSmall,
            color = theme.textSecondary,
            fontSize = 9.sp
        )
        for (color in listOf(
            theme.divider,
            HeatmapGreen1,
            HeatmapGreen2,
            HeatmapGreen3,
            HeatmapGreen5
        )) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
        Text(
            text = "More",
            style = MaterialTheme.typography.labelSmall,
            color = theme.textSecondary,
            fontSize = 9.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(HeatmapRelapse)
        )
        Text(
            text = "Relapse",
            style = MaterialTheme.typography.labelSmall,
            color = theme.textSecondary,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun DateFilterChips(
    selectedFilter: DateFilter,
    onFilterSelected: (DateFilter) -> Unit
) {
    val theme = LocalTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (filter in DateFilter.entries) {
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.label,
                        color = if (selectedFilter == filter) theme.textPrimary
                        else theme.textSecondary
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = theme.buttonPrimary,
                    containerColor = theme.cardContainer,
                    selectedLabelColor = theme.textPrimary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = theme.divider,
                    selectedBorderColor = theme.buttonPrimary,
                    enabled = true,
                    selected = selectedFilter == filter
                )
            )
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
                        enabled = false,
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

@OptIn(ExperimentalTime::class)
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
                date = it.relapseDate.formatDate(dateFormat2)
            )
        }
    }
    val heatmapData = remember {
        val today =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startDate = today.minus(DatePeriod(days = 364))
        val days = mutableListOf<HeatmapDay>()
        var streak = 0
        var date = startDate
        while (date <= today) {
            val isRelapse = date.dayOfMonth == 15 && date.monthNumber % 3 == 0
            if (isRelapse) streak = 0
            days.add(HeatmapDay(date, streak, isRelapse))
            if (!isRelapse) streak++
            date = date.plus(DatePeriod(days = 1))
        }
        days
    }
    MaterialTheme {
        AnalyticsScreen(
            uiState = AnalyticsState(
                isLoading = false,
                chartData = chartData,
                streakData = streakData,
                heatmapData = heatmapData
            )
        )
    }
}
