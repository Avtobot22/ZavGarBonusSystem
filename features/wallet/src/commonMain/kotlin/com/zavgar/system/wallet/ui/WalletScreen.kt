package com.zavgar.system.wallet.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.designsystem.components.content.AnimatedState
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.modifiers.ShackingState
import com.zavgar.system.designsystem.modifiers.rememberShackingState
import com.zavgar.system.designsystem.screen.ErrorScreen
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_top_title_wallet
import com.zavgar.system.wallet.presentation.WalletEvent
import com.zavgar.system.wallet.presentation.WalletIntent
import com.zavgar.system.wallet.presentation.WalletState
import com.zavgar.system.wallet.presentation.WalletViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun WalletScreen(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    WalletLoader(
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier
    )
}

@Composable
internal fun WalletLoader(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier,
    viewModel: WalletViewModel = koinInject()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val errorShakingState = rememberShackingState()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is WalletEvent.NavigateToLogin -> onNavigateToLogin()
            is WalletEvent.ShowSnackbar -> {
                launch {
                    errorShakingState.shake()
                }
                launch {
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
        modifier = modifier
    )
}

@Composable
internal fun WalletScaffold(
    state: WalletState,
    snackbarHostState: SnackbarHostState,
    onIntent: (WalletIntent) -> Unit,
    errorShakingState: ShackingState,
    modifier: Modifier
) {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.home_top_title_wallet),
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    ) { paddingValues ->
        AnimatedState(targetState = state) { state ->
            when (val screenState = state.screenState) {
                WalletState.ScreenState.Initial,
                WalletState.ScreenState.Loading -> WalletLoading(
                    modifier = Modifier.padding(paddingValues)
                )

                is WalletState.ScreenState.Content -> {
                    PullToRefreshBox(
                        isRefreshing = screenState.isRefreshing,
                        onRefresh = { onIntent(WalletIntent.PullToRefresh) },
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        WalletContent(
                            state = state,
                            screenState = screenState,
                            onIntent = onIntent,
                            modifier = Modifier
                        )
                    }
                }

                WalletState.ScreenState.Error -> ErrorScreen(
                    onRetry = { onIntent(WalletIntent.Retry) },
                    modifier = Modifier.padding(paddingValues),
                    shakingState = errorShakingState
                )

                WalletState.ScreenState.Offline -> {
                    PullToRefreshBox(
                        isRefreshing = false,
                        onRefresh = { onIntent(WalletIntent.PullToRefresh) },
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        WalletOfflineContent(
                            phone = state.phone,
                            modifier = Modifier
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
                        isRefreshing = false
                    ),
                    phone = "+7 (999) 123-45-67",
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier,
                errorShakingState = rememberShackingState()
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
                errorShakingState = rememberShackingState()
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
                errorShakingState = rememberShackingState()
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
                errorShakingState = rememberShackingState()
            )
        }
    }
}
