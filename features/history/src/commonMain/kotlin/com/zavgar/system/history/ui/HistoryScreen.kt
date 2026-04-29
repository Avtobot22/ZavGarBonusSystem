package com.zavgar.system.history.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import com.zavgar.system.core.presentation.compose.Pagination
import com.zavgar.system.designsystem.components.chip.AppDateChip
import com.zavgar.system.designsystem.components.content.AnimatedState
import com.zavgar.system.designsystem.components.content.AppProgressIndicator
import com.zavgar.system.designsystem.components.datepicker.AppDatePicker
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.screen.ErrorScreen
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
    val errorShakingState = rememberShakingState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is HistoryEvent.Logout -> onNavigateToLogin()
            is HistoryEvent.ShowSnackbar -> {
                scope.launch {
                    errorShakingState.shake()
                }

                scope.launch {
                    snackbarHostState.showCustomSnackbar(
                        type = event.message.type,
                        message = event.message.message.suspendAsString(),
                        withDismissAction = true,
                    )
                }
            }
        }
    }

    HistoryScaffold(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        errorShakingState = errorShakingState,
        modifier = modifier
    )
}

@Composable
internal fun HistoryScaffold(
    state: HistoryState,
    onIntent: (HistoryIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    errorShakingState: ShakingState,
    modifier: Modifier
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val historyNotEmpty = state.history.isNotEmpty()
    val isLoadingFirstPage = state.screenState is HistoryState.ScreenState.Loading
            || state.screenState is HistoryState.ScreenState.Initial
            || state.screenState is HistoryState.ScreenState.Reloading

    val showFab by remember(historyNotEmpty, isLoadingFirstPage) {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2 && historyNotEmpty && !isLoadingFirstPage
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.home_title_history),
                modifier = Modifier.padding(top = 24.dp)
            )
        },
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
        AnimatedState(targetState = state) { state ->
            when (state.screenState) {
                HistoryState.ScreenState.Error -> ErrorScreen(
                    onRetry = { onIntent(HistoryIntent.Retry) },
                    modifier = Modifier.padding(paddingValues),
                    shakingState = errorShakingState
                )

                HistoryState.ScreenState.Initial -> HistoryFullScreenLoading(paddingValues)

                HistoryState.ScreenState.Loading -> HistoryFullScreenLoading(paddingValues)

                else -> HistoryContent(
                    state = state,
                    onIntent = onIntent,
                    listState = listState,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun HistoryFullScreenLoading(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        AppProgressIndicator()
    }
}

@Composable
internal fun HistoryContent(
    state: HistoryState,
    onIntent: (HistoryIntent) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Pagination(itemsCount = state.history.size, lazyListState = listState) {
        onIntent(HistoryIntent.LoadNextPage)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DateChips(state, onIntent)

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(HistoryIntent.Refresh) },
            modifier = Modifier.fillMaxSize()
        ) {
            HistoryLazyList(state, scrollState, listState)
        }
    }
}

@Composable
private fun HistoryLazyList(
    state: HistoryState,
    scrollState: ScrollState,
    listState: LazyListState
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        shadowElevation = 8.dp,
    ) {
        if (state.screenState is HistoryState.ScreenState.Reloading) {
            LoadingHistoryList()
        } else if (state.history.isEmpty()) {
            EmptyHistoryContent(
                Modifier.verticalScroll(scrollState),
            )
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

@Composable
private fun DateChips(
    state: HistoryState,
    onIntent: (HistoryIntent) -> Unit
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
        screenState = HistoryState.ScreenState.Content,
        history = sampleHistoryItems,
        periodStartText = "01.12.2023",
        periodEndText = "31.12.2023",
        isRefreshing = false,
        isLoadingNextPage = false
    )
    ZavGarThemePreview {
        Screen {
            HistoryScaffold(
                state = previewState,
                onIntent = {},
                snackbarHostState = remember { SnackbarHostState() },
                modifier = Modifier,
                errorShakingState = rememberShakingState()
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
