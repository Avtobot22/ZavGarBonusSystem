package com.zavgar.system.shared

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.zavgar.system.appstate.ZavGarAppState
import com.zavgar.system.appstate.rememberZavGarAppState
import com.zavgar.system.datastore.datasource.ThemeDataSource
import com.zavgar.system.designsystem.theme.ZavGarTheme
import com.zavgar.system.events.AppEvent
import com.zavgar.system.events.AppEventBus
import com.zavgar.system.home.Home
import com.zavgar.system.navigation.compose.Navigation
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.event.SettingsEvent
import org.koin.compose.koinInject

@Composable
fun ZavGarMultiplatformApp(
    modifier: Modifier = Modifier,
    appState: ZavGarAppState = rememberZavGarAppState(),
    appEventBus: AppEventBus = koinInject(),
    navEventController: NavEventController = koinInject(),
    themeDataSource: ThemeDataSource = koinInject(),
) {
    val isDarkTheme by themeDataSource.isDarkTheme.collectAsState(false)

    LaunchedEffect(appEventBus) {
        appEventBus.events.collect { event ->
            when (event) {
                AppEvent.Logout -> navEventController.sendEvent(SettingsEvent.Logout)
            }
        }
    }

    ZavGarTheme(isDarkTheme = isDarkTheme) {
        Home(
            appState = appState,
            modifier = modifier,
            content = { paddingValues ->
                Navigation(
                    navBackStack = appState.navBackStack,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                )
            }
        )
    }
}