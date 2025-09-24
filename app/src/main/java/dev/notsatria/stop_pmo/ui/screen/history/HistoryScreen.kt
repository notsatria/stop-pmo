package dev.notsatria.stop_pmo.ui.screen.history

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.notsatria.stop_pmo.R
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.components.HistoryItem
import dev.notsatria.stop_pmo.ui.components.HistoryItemType
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.DummyData
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryRoute(modifier: Modifier = Modifier, viewModel: HistoryViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(
        modifier = modifier,
        uiState = uiState,
        onLoadHistory = {
            viewModel.loadNextPage()
        }
    )
}

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    uiState: HistoryUiState = HistoryUiState(),
    onLoadHistory: () -> Unit = {},
) {
    val theme = LocalTheme.current
    val listState = rememberLazyListState()
    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = listState.layoutInfo.totalItemsCount
            Pair(lastVisible, total)
        }.distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (lastVisible < 0 || total == 0) return@collect
                val prefetch = 1
                val shouldLoadMore = lastVisible >= total - prefetch
                if (shouldLoadMore && !uiState.isLoading && !uiState.pageState.endReached) {
                    onLoadHistory()
                }
            }
    }
    Scaffold(
        modifier,
        topBar = {
            CenterTopBar(title = "Relapase History")
        },
        containerColor = theme.surface
    ) { innerPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding), state = listState
        ) {
            itemsIndexed(uiState.relapseHistory) { index, history ->
                HistoryItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .padding(horizontal = 20.dp),
                    occurredAt = history.occurredAt,
                    type = HistoryItemType.HISTORY
                )
                if (index == uiState.relapseHistory.lastIndex) {
                    Box(modifier = Modifier.height( 20.dp))
                }
            }
            item("footer") {
                when {
                    uiState.isLoading -> Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    uiState.pageState.endReached -> Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No more data",
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = theme.textSecondary
                            )
                        )
                    }
                }
            }
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
        HistoryScreen(uiState = HistoryUiState(DummyData.generateRecentRelapses()))
    }
}