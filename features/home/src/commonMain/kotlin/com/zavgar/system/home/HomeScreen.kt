package com.zavgar.system.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.zavgar.system.appstate.ZavGarAppState
import com.zavgar.system.navigationapi.transition.BottomBarEnterTransition
import com.zavgar.system.navigationapi.transition.BottomBarExitTransition
import com.zavgar.system.navigationapi.bottombar.AppBottomBar
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.TopLevelDestinations
import com.zavgar.system.navigationapi.event.HomeEvent
import com.zavgar.system.navigationapi.marker.TopLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.koinInject

@Composable
fun Home(
    appState: ZavGarAppState,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeLoader(
        appState = appState,
        content = content,
        modifier = modifier,
    )
}

@Composable
private fun HomeLoader(
    appState: ZavGarAppState,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier,
    navEventController: NavEventController = koinInject(),
) {
    val navItems = remember { TopLevelDestinations.toImmutableList() }

    val currentSection = appState.navBackStack.topLevelKey as? TopLevel

    val lastValidSection = remember(currentSection) { currentSection } ?: TopLevelDestinations.firstOrNull()

    val setCurrentState = { section: TopLevel ->
        navEventController.sendEvent(HomeEvent.OnTabClick(section))
    }

    HomeScaffold(
        navItems = navItems,
        isBottomBarVisible = appState.navBackStack.isBottomBarVisible,
        lastValidSection = lastValidSection,
        setCurrentState = setCurrentState,
        content = content,
        modifier = modifier,
    )
}

@Composable
private fun HomeScaffold(
    navItems: ImmutableList<TopLevel>,
    isBottomBarVisible: Boolean,
    lastValidSection: TopLevel?,
    setCurrentState: (TopLevel) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = BottomBarEnterTransition,
                exit = BottomBarExitTransition,
            ) {
                lastValidSection?.let { section ->
                    AppBottomBar(
                        items = navItems,
                        currentSection = section,
                        setCurrentSection = setCurrentState
                    )
                }
            }
        },
    ) { paddingValues ->
        content(paddingValues)
    }
}