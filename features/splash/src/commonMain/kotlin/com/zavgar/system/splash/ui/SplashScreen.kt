package com.zavgar.system.splash.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zavgar.system.core.presentation.ObserveAsEvents
import com.zavgar.system.designsystem.components.logo.AppLogo
import androidx.compose.material3.MaterialTheme
import com.zavgar.system.designsystem.theme.*
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
    SplashContent(
        state = state,
        modifier = modifier
    )
}

@Composable
private fun SplashContent(state: SplashState, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.accent),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AppLogo()

            Spacer(Modifier.height(52.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = colors.onAccent,
                strokeWidth = 3.dp,
            )
        }
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