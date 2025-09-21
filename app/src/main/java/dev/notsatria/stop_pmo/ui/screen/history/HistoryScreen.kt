package dev.notsatria.stop_pmo.ui.screen.history

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.components.Calendar
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.DummyData

@Composable
fun HistoryRoute(modifier: Modifier = Modifier) {
    HistoryScreen()
}

@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    val theme = LocalTheme.current
    Scaffold(
        topBar = {
            CenterTopBar(title = "History")
        },
        containerColor = theme.surface
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MonthYearRow()
            Spacer(Modifier.height(8.dp))
            Calendar(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxSize()
                    .aspectRatio(1.3f),
                calendarInput = DummyData.generateCalendarInputList(),
                onDayClick = {},
                strokeWidth = 4f
            )
            Text("halo")
        }
    }
}

@Composable
private fun MonthYearRow(
    modifier: Modifier = Modifier,
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    date: String = "Sept 2025"
) {
    val theme = LocalTheme.current
    Row(modifier) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = "Previous month",
                tint = theme.iconPrimary
            )
        }
        Text(
            date,
            modifier = Modifier.align(Alignment.CenterVertically),
            style = TextStyle(fontWeight = FontWeight.SemiBold, color = theme.textPrimary)
        )
        IconButton(onClick = onNextClick) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = "Previous month",
                tint = theme.iconPrimary
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HistoryScreenPreview(modifier: Modifier = Modifier) {
    MaterialTheme {
        HistoryScreen()
    }
}