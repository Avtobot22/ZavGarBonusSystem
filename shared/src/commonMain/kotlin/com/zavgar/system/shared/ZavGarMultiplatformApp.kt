package com.zavgar.system.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zavgar.system.appstate.ZavGarAppState
import com.zavgar.system.appstate.rememberZavGarAppState
import com.zavgar.system.designsystem.theme.ZavGarTheme
import com.zavgar.system.home.Home

@Composable
fun ZavGarMultiplatformApp(
    modifier: Modifier = Modifier,
    appState: ZavGarAppState = rememberZavGarAppState(),
) {
    ZavGarTheme {
        Home(
            appState = appState,
            modifier = modifier
        )
    }
}