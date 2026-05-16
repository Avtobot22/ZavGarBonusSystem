package com.zavgar.system.history.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.compose.Pagination
import com.zavgar.system.designsystem.components.content.AnimatedState
import com.zavgar.system.designsystem.components.content.AppProgressIndicator
import com.zavgar.system.designsystem.components.datepicker.AppDatePicker
import com.zavgar.system.designsystem.components.scaffold.ZavGarBaseScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.screen.ErrorScreen
import com.zavgar.system.designsystem.screen.Screen
import androidx.compose.material3.MaterialTheme
import com.zavgar.system.designsystem.theme.*
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.history.model.History
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
    val colors = MaterialTheme.colorScheme
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val historyNotEmpty = state.history.transactions.isNotEmpty()
    val isLoadingFirstPage = state.screenState is HistoryState.ScreenState.Loading
            || state.screenState is HistoryState.ScreenState.Initial
            || state.screenState is HistoryState.ScreenState.Reloading

    val showFab by remember(historyNotEmpty, isLoadingFirstPage) {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2 && historyNotEmpty && !isLoadingFirstPage
        }
    }

    ZavGarBaseScaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
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
                    containerColor = colors.accentSoft,
                    contentColor = colors.accent,
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Наверх"
                    )
                }
            }
        }
    ) { paddingValues ->
        AnimatedState(targetState = state, contentKey = { it.screenState::class }) { state ->
            when (state.screenState) {
                HistoryState.ScreenState.Error -> ErrorScreen(
                    onRetry = { onIntent(HistoryIntent.Retry) },
                    modifier = Modifier.padding(paddingValues),
                    shakingState = errorShakingState
                )

                HistoryState.ScreenState.Initial,

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

    Pagination(itemsCount = state.history.transactions.size, lazyListState = listState) {
        onIntent(HistoryIntent.LoadNextPage)
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HistoryHeader(state = state, onIntent = onIntent)

        Spacer(Modifier.height(14.dp))

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(HistoryIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            HistoryListCard(state, scrollState, listState)
        }
    }
}

@Composable
private fun HistoryHeader(
    state: HistoryState,
    onIntent: (HistoryIntent) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 4.dp),
    ) {
        Text(
            text = stringResource(Res.string.home_title_history),
            color = colors.foreground,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            DatePill(
                label = stringResource(Res.string.history_period_start),
                value = state.periodStartText,
                onClick = { onIntent(HistoryIntent.OpenDatePicker(DatePickerType.START)) },
                modifier = Modifier.weight(1f),
            )
            DatePill(
                label = stringResource(Res.string.history_period_end),
                value = state.periodEndText,
                onClick = { onIntent(HistoryIntent.OpenDatePicker(DatePickerType.END)) },
                modifier = Modifier.weight(1f),
            )
        }

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

@Composable
private fun DatePill(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = shape, clip = false)
            .clip(shape)
            .background(colors.card)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "$label ${value.ifBlank { "—" }}",
            color = colors.foreground,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun HistoryListCard(
    state: HistoryState,
    scrollState: ScrollState,
    listState: LazyListState,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .shadow(elevation = 6.dp, shape = shape, clip = false)
            .clip(shape)
            .background(colors.card),
        contentAlignment = Alignment.Center
    ) {
        if (state.screenState is HistoryState.ScreenState.Reloading) {
            LoadingHistoryList()
        } else if (state.history.transactions.isEmpty()) {
            EmptyHistoryContent(
                Modifier.verticalScroll(scrollState),
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, bottom = 12.dp),
            ) {
                items(
                    items = state.history.transactions,
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
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
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
        HistoryItem.DateHeader(id = "header1", date = "24.04.2026"),
        HistoryItem.TransactionItem(
            id = "1",
            store = "АЗС Нефтемаг",
            time = "Заправка · 50 л",
            amount = "+150 б",
            isIncome = true,
        ),
        HistoryItem.DateHeader(id = "header2", date = "22.04.2026"),
        HistoryItem.TransactionItem(
            id = "2",
            store = "Автосервис ZavGar",
            time = "Замена масла",
            amount = "−80 б",
            isIncome = false,
        ),
    )

    val previewState = HistoryState(
        screenState = HistoryState.ScreenState.Content,
        history = History(
            transactions = sampleHistoryItems,
            nextCursor = null,
            hasMore = false,
        ),
        periodStartText = "10.11.2025",
        periodEndText = "24.04.2026",
        isRefreshing = false,
        isLoadingNextPage = false,
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
