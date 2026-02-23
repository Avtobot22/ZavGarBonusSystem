package com.zavgar.system.wallet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.designsystem.components.button.AppOutlinedButton
import com.zavgar.system.designsystem.components.qrcode.AppQrCode
import com.zavgar.system.designsystem.components.qrcode.LoadingQrCode
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_top_title_wallet
import com.zavgar.system.resources.refresh_points
import com.zavgar.system.wallet.presentation.WalletEvent
import com.zavgar.system.wallet.presentation.WalletIntent
import com.zavgar.system.wallet.presentation.WalletState
import com.zavgar.system.wallet.presentation.WalletViewModel
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
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is WalletEvent.NavigateToLogin -> onNavigateToLogin()
            is WalletEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = event.message.suspendAsString(),
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    WalletScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        modifier = modifier
    )
}

@Composable
internal fun WalletScaffold(
    state: WalletState,
    snackbarHostState: SnackbarHostState,
    onIntent: (WalletIntent) -> Unit,
    modifier: Modifier
) {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        when (state.screenState) {
            WalletState.ScreenState.Loading -> WalletLoading(
                modifier = Modifier.padding(paddingValues)
            )

            WalletState.ScreenState.Content -> WalletContent(
                state = state,
                onIntent = onIntent,
                modifier = Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            )
        }
    }
}

@Composable
fun WalletContent(state: WalletState, onIntent: (WalletIntent) -> Unit, modifier: Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        AppTopBar(
            title = stringResource(Res.string.home_top_title_wallet),
            modifier = Modifier.padding(top = 48.dp, bottom = 20.dp)
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppQrCode(
                card = state.phone,
                points = state.balance,
            )

            AppOutlinedButton(
                onClick = { onIntent(WalletIntent.RefreshBalance) },
                enabled = !state.isRefreshing,
                isLoading = state.isRefreshing,
                modifier = Modifier.fillMaxWidth(0.7f),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier
                    )

                    Spacer(Modifier.width(5.dp))

                    Text(
                        stringResource(Res.string.refresh_points),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
internal fun WalletLoading(modifier: Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        AppTopBar(
            title = stringResource(Res.string.home_top_title_wallet),
            modifier = Modifier.padding(top = 48.dp, bottom = 20.dp)
        )


        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoadingQrCode()

            AppOutlinedButton(
                onClick = {},
                enabled = false,
                isLoading = true,
                modifier = Modifier.fillMaxWidth(0.7f),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 16.dp)
            ) {}
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
                    screenState = WalletState.ScreenState.Content,
                    phone = "+7 (999) 123-45-67",
                    balance = 1250,
                    isRefreshing = false
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier
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
                    balance = 0,
                    isRefreshing = false
                ),
                snackbarHostState = remember { SnackbarHostState() },
                onIntent = {},
                modifier = Modifier
            )
        }
    }
}
