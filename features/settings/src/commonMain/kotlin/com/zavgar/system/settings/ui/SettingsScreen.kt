package com.zavgar.system.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import com.zavgar.system.designsystem.components.button.AppMenuButton
import com.zavgar.system.designsystem.components.snackbar.CustomSnackbarHost
import com.zavgar.system.designsystem.components.snackbar.showCustomSnackbar
import com.zavgar.system.designsystem.components.topbar.AppTopBar
import com.zavgar.system.designsystem.screen.Screen
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.home_title_setting
import com.zavgar.system.resources.settings_logout_button
import com.zavgar.system.resources.settings_profil_details_button
import com.zavgar.system.settings.presentation.SettingsEvent
import com.zavgar.system.settings.presentation.SettingsIntent
import com.zavgar.system.settings.presentation.SettingsState
import com.zavgar.system.settings.presentation.SettingsViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsLoader(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToProfileDetail = onNavigateToProfileDetail,
        modifier = modifier
    )
}

@Composable
internal fun SettingsLoader(
    onNavigateToLogin: () -> Unit,
    onNavigateToProfileDetail: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is SettingsEvent.NavigateToProfileDetail -> onNavigateToProfileDetail()
            is SettingsEvent.NavigateToLogin -> onNavigateToLogin()
            is SettingsEvent.ShowSnackbar -> {
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

    SettingsScaffold(
        state = state,
        onIntent = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
internal fun SettingsScaffold(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { CustomSnackbarHost(snackbarHostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(Res.string.home_title_setting),
                modifier = Modifier.padding(top = 24.dp)
            )
        },
    ) { paddingValues ->

        when (state) {
            is SettingsState.Content -> SettingsContent(
                state = state,
                onIntent = onIntent,
                modifier = Modifier
                    .padding(paddingValues)
            )

            is SettingsState.Loading -> SettingsLoading(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
internal fun SettingsContent(
    state: SettingsState.Content,
    onIntent: (SettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppMenuButton(
                text = stringResource(Res.string.settings_profil_details_button),
                icon = Icons.Default.Person,
                onClick = { onIntent(SettingsIntent.ToProfileDetail) }
            )

            AppMenuButton(
                text = stringResource(Res.string.settings_logout_button),
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = { onIntent(SettingsIntent.Logout) },
                isDestructive = true
            )
        }
    }
}

@Composable
internal fun SettingsLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun SettingsScaffoldPreview() {
    val mockState = SettingsState.Content
    val mockSnackbarHostState = remember { SnackbarHostState() }

    ZavGarThemePreview {
        Screen {
            SettingsScaffold(
                state = mockState,
                onIntent = { },
                snackbarHostState = mockSnackbarHostState
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScaffoldLoadingPreview() {
    val mockState = SettingsState.Loading
    val mockSnackbarHostState = remember { SnackbarHostState() }

    ZavGarThemePreview {
        Screen {
            SettingsScaffold(
                state = mockState,
                onIntent = { },
                snackbarHostState = mockSnackbarHostState
            )
        }
    }
}
