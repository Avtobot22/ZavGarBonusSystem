package com.zavgar.system.splash.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.designsystem.components.logo.AppLogo
import com.zavgar.system.designsystem.theme.ZavGarThemePreview
import com.zavgar.system.splash.presentation.SplashEvent
import com.zavgar.system.splash.presentation.SplashState
import com.zavgar.system.splash.presentation.SplashViewModel
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier = Modifier
) {
    SplashLoader(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToWallet = onNavigateToWallet,
        modifier = modifier
    )
}

@Composable
private fun SplashLoader(
    onNavigateToLogin: () -> Unit,
    onNavigateToWallet: () -> Unit,
    modifier: Modifier,
    viewModel: SplashViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.ObserveAsEvents { event ->
        when (event) {
            is SplashEvent.NavigateToLogin -> onNavigateToLogin()
            is SplashEvent.NavigateToWallet -> onNavigateToWallet()
        }
    }

    SplashScaffold(
        state = state,
        modifier = modifier
    )
}

@Composable
private fun SplashScaffold(
    state: SplashState,
    modifier: Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.primary
    ) { _ ->
        SplashContent(
            state = state,
            modifier = Modifier
        )
    }
}

@Composable
private fun SplashContent(state: SplashState, modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AppLogo()
    }
}

@Preview
@Composable
private fun SplashScaffoldPreview() {
    ZavGarThemePreview {
        SplashScaffold(
            state = SplashState,
            modifier = Modifier
        )
    }
}