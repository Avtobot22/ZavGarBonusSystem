package com.zavgar.system.designsystem.components.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zavgar.system.designsystem.background.GlowBackground

/**
 * Унифицированный каркас для экранов main-флоу (Wallet / History / Settings / Account).
 */
@Composable
fun ZavGarBaseScaffold(
    modifier: Modifier = Modifier,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    applyStatusBarsPadding: Boolean = true,
    content: @Composable (PaddingValues) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .then(if (applyStatusBarsPadding) Modifier.statusBarsPadding() else Modifier),
        contentWindowInsets = WindowInsets(),
        containerColor = colors.background,
        snackbarHost = {
            Box(Modifier.navigationBarsPadding()) { snackbarHost() }
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
    ) { padding ->
        GlowBackground { content(padding) }
    }
}