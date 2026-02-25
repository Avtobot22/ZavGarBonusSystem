package com.zavgar.system.history.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.designsystem.components.chip.AppDateChip
import com.zavgar.system.designsystem.components.datepicker.AppDatePicker
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.history.model.HistoryItem
import com.zavgar.system.history.presentation.HistoryEvent
import com.zavgar.system.history.presentation.HistoryIntent
import com.zavgar.system.history.presentation.HistoryState
import com.zavgar.system.history.presentation.HistoryViewModel
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.history_period_end
import com.zavgar.system.resources.history_period_start
import com.zavgar.system.resources.home_title_history
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

const val LOAD_MORE_THRESHOLD = 5

@Composable
fun HistoryScreen(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    HistoryLoader(
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )
}

@Composable
internal fun HistoryLoader(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is HistoryEvent.Logout -> onNavigateToLogin()
            is HistoryEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.message.suspendAsString(),
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    HistoryScaffold(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
internal fun HistoryScaffold(
    state: HistoryState,
    onIntent: (HistoryIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val showFab by remember { derivedStateOf { listState.firstVisibleItemIndex > 2 } }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Наверх"
                    )
                }
            }
        }
    ) { paddingValues ->
        HistoryContent(
            state = state,
            onIntent = onIntent,
            listState = listState,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
internal fun HistoryContent(
    state: HistoryState,
    onIntent: (HistoryIntent) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 && lastVisibleItem >= totalItems - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onIntent(HistoryIntent.LoadNextPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppTopBar(
            title = stringResource(Res.string.home_title_history),
            modifier = Modifier.padding(top = 60.dp)
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppDateChip(
                    value = state.periodStartText,
                    label = stringResource(Res.string.history_period_start),
                    onClick = { onIntent(HistoryIntent.OpenDatePicker(DatePickerType.START)) },
                )
                AppDateChip(
                    value = state.periodEndText,
                    label = stringResource(Res.string.history_period_end),
                    onClick = { onIntent(HistoryIntent.OpenDatePicker(DatePickerType.END)) },
                )

                AppDatePicker(
                    initialDate = if (state.datePickerOpen == DatePickerType.START) state.periodStart else state.periodEnd,
                    isOpen = state.datePickerOpen != null,
                    onDismiss = { onIntent(HistoryIntent.DismissDatePicker) },
                    onConfirm = {
                        onIntent(
                            HistoryIntent.CloseDatePicker(
                                if (state.datePickerOpen == DatePickerType.START) DatePickerType.START else DatePickerType.END,
                                it
                            )
                        )
                    }
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onIntent(HistoryIntent.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    shadowElevation = 8.dp,
                ) {
                    if (state.isLoadingFirstPage) {
                        LoadingHistoryList()
                    } else if (state.history.isEmpty()) {
                        EmptyHistoryContent()
                    } else {

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 10.dp),
                        ) {
                            items(
                                items = state.history,
                                key = { item -> item.id }
                            ) { item ->
                                when (item) {
                                    is HistoryItem.DateHeader -> DateHeaderItem(item)
                                    is HistoryItem.TransactionItem -> TransactionItem(item)
                                }
                            }

                            if (state.isLoadingNextPage) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScaffoldPreview() {
    val sampleHistoryItems = listOf(
        HistoryItem.DateHeader(
            id = "header1",
            date = "15 декабря 2023"
        ),
        HistoryItem.TransactionItem(
            id = "1",
            store = "Магазин продуктов",
            time = "14:30",
            amount = "+150 ₽",
            isIncome = true
        ),
        HistoryItem.TransactionItem(
            id = "2",
            store = "Кафе",
            time = "18:45",
            amount = "-320 ₽",
            isIncome = false
        ),
        HistoryItem.DateHeader(
            id = "header2",
            date = "14 декабря 2023"
        ),
        HistoryItem.TransactionItem(
            id = "3",
            store = "Аптека",
            time = "12:15",
            amount = "-85 ₽",
            isIncome = false
        )
    )

    val previewState = HistoryState(
        history = sampleHistoryItems,
        periodStartText = "01.12.2023",
        periodEndText = "31.12.2023",
        isLoadingFirstPage = false,
        isRefreshing = false,
        isLoadingNextPage = false
    )
    ZavGarThemePreview {
        Screen {
            HistoryScaffold(
                state = previewState,
                onIntent = {},
                snackbarHostState = remember { SnackbarHostState() },
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingHistoryListPreview() {
    ZavGarThemePreview {
        Screen {
            LoadingHistoryList()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyHistoryContentPreview() {
    ZavGarThemePreview {
        Screen {
            EmptyHistoryContent()
        }
    }
}

