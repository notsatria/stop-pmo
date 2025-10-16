package dev.notsatria.stop_pmo.ui.screen.history

import android.R.attr.type
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.notsatria.stop_pmo.ui.components.CenterTopBar
import dev.notsatria.stop_pmo.ui.components.EmptyStateView
import dev.notsatria.stop_pmo.ui.components.HistoryItem
import dev.notsatria.stop_pmo.ui.components.HistoryItemType
import dev.notsatria.stop_pmo.ui.theme.LocalTheme
import dev.notsatria.stop_pmo.utils.DummyData
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel(),
    onNavigateToDashboard: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUserSettings()
    }

    HistoryScreen(
        modifier = modifier,
        uiState = uiState,
        onLoadHistory = {
            viewModel.loadNextPage()
        },
        onRelapseClick = onNavigateToDashboard
    )
}

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    uiState: HistoryUiState = HistoryUiState(),
    onLoadHistory: () -> Unit = {},
    onRelapseClick: () -> Unit = {}
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
        modifier.fillMaxSize(),
        topBar = {
            CenterTopBar(title = "Relapse History")
        },
        containerColor = theme.surface
    ) { innerPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = listState,
            verticalArrangement = if (uiState.relapseHistory.isEmpty()) Arrangement.Center else Arrangement.Top
        ) {
            if (!uiState.relapseHistory.isEmpty()) {
                item { Spacer(Modifier.height(20.dp)) }
            }
            itemsIndexed(uiState.relapseHistory) { index, history ->
                HistoryItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .padding(horizontal = 20.dp),
                    occurredAt = history.occurredAt,
                    streak = history.streak,
                    type = HistoryItemType.HISTORY,
                    use24HourFormat = uiState.use24HourFormat
                )
                if (index == uiState.relapseHistory.lastIndex) {
                    Box(modifier = Modifier.height(20.dp))
                }
            }

            if (uiState.relapseHistory.isEmpty()) {
                item {
                    EmptyStateView(
                        onRelapseClick = onRelapseClick,
                        isButtonRelapseVisible = true
                    )
                }
            }
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

@Preview
@Composable
private fun EmptyPreview() {
    MaterialTheme {
        HistoryScreen { }
    }
}