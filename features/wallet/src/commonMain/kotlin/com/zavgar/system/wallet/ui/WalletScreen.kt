package com.zavgar.system.wallet.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.core.presentation.compose.ScreenEntryEffect
import com.zavgar.system.designsystem.components.content.AnimatedState
import com.zavgar.system.designsystem.components.scaffold.ZavGarBaseScaffold
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.modifiers.ShakingState
import com.zavgar.system.designsystem.modifiers.rememberShakingState
import com.zavgar.system.designsystem.screen.ErrorScreen
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.wallet.presentation.WalletEvent
import com.zavgar.system.wallet.presentation.WalletIntent
import com.zavgar.system.wallet.presentation.WalletState
import com.zavgar.system.wallet.presentation.WalletViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WalletScreen(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WalletLoader(
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier,
    )
}

@Composable
internal fun WalletLoader(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier,
    viewModel: WalletViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShakingState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ScreenEntryEffect(viewModel) {
        viewModel.handleIntent(WalletIntent.ScreenEntered)
    }

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is WalletEvent.NavigateToLogin -> onNavigateToLogin()
            is WalletEvent.ShowSnackbar -> {
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

    WalletScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        errorShakingState = errorShakingState,
        modifier = modifier,
    )
}

@Composable
internal fun WalletScaffold(
    state: WalletState,
    snackbarHostState: SnackbarHostState,
    onIntent: (WalletIntent) -> Unit,
    errorShakingState: ShakingState,
    modifier: Modifier,
) {
    ZavGarBaseScaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
    ) { paddingValues ->
        AnimatedState(targetState = state, contentKey = { it.screenState::class }) { state ->
            when (val screenState = state.screenState) {
                WalletState.ScreenState.Initial,
                WalletState.ScreenState.Loading,
                -> WalletLoading(
                    modifier = Modifier.padding(paddingValues),
                )

                is WalletState.ScreenState.Content -> {
                    PullToRefreshBox(
                        isRefreshing = screenState.isRefreshing,
                        onRefresh = { onIntent(WalletIntent.PullToRefresh) },
                        modifier = Modifier.padding(paddingValues),
                    ) {
                        WalletContent(
                            state = state,
                            screenState = screenState,
                            onIntent = onIntent,
                            modifier = Modifier,
                        )
                    }
                }

                WalletState.ScreenState.Error -> ErrorScreen(
                    onRetry = { onIntent(WalletIntent.Retry) },
                    modifier = Modifier.padding(paddingValues),
                    shakingState = errorShakingState,
                )

                WalletState.ScreenState.Offline -> {
                    PullToRefreshBox(
                        isRefreshing = false,
                        onRefresh = { onIntent(WalletIntent.PullToRefresh) },
                        modifier = Modifier.padding(paddingValues),
                    ) {
                        WalletOfflineContent(
                            phone = state.phone,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun WalletScaffoldContentPreview() {
    ZavGarThemePreview {
        Screen {
            WalletScaffold(
                state = WalletState(
                    screenState = WalletState.ScreenState.Content(
                        balance = 1250,
                        isRefreshing = false,
                    ),
                    phone = "+7 (999) 123-45-67",
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState(),
            )
        }
    }
}

@Preview
@Composable
private fun WalletScaffoldLoadingPreview() {
    ZavGarThemePreview {
        Screen {
            WalletScaffold(
                state = WalletState(
                    screenState = WalletState.ScreenState.Loading,
                    phone = "",
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState(),
            )
        }
    }
}

@Preview
@Composable
private fun WalletScaffoldErrorPreview() {
    ZavGarThemePreview {
        Screen {
            WalletScaffold(
                state = WalletState(
                    screenState = WalletState.ScreenState.Error,
                    phone = "",
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState(),
            )
        }
    }
}

@Preview
@Composable
private fun WalletScaffoldOfflinePreview() {
    ZavGarThemePreview {
        Screen {
            WalletScaffold(
                state = WalletState(
                    screenState = WalletState.ScreenState.Offline,
                    phone = "+7 (999) 123-45-67",
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShakingState(),
            )
        }
    }
}
