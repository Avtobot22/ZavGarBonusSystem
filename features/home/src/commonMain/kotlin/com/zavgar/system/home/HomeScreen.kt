package com.zavgar.system.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.zavgar.system.appstate.ZavGarAppState
import com.zavgar.system.designsystem.animation.BottomBarEnterTransition
import com.zavgar.system.designsystem.animation.BottomBarExitTransition
import com.zavgar.system.designsystem.background.GlowBackground
import com.zavgar.system.navigation.compose.Navigation
import com.zavgar.system.navigationapi.bottombar.AppBottomBar
import com.zavgar.system.navigationapi.controller.NavBackStack
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.TopLevelDestinations
import com.zavgar.system.navigationapi.event.HomeEvent
import com.zavgar.system.navigationapi.marker.TopLevel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.koinInject

@Composable
fun Home(
    appState: ZavGarAppState,
    modifier: Modifier = Modifier
) {
    HomeLoader(
        appState = appState,
        modifier = modifier
    )
}

@Composable
private fun HomeLoader(
    appState: ZavGarAppState,
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
        isEdgeToEdge = appState.navBackStack.isEdgeToEdge,
        lastValidSection = lastValidSection,
        setCurrentState = setCurrentState,
        navBackStack = appState.navBackStack,
        modifier = modifier,
    )
}


@Composable
private fun HomeScaffold(
    navItems: ImmutableList<TopLevel>,
    isBottomBarVisible: Boolean,
    isEdgeToEdge: Boolean,
    lastValidSection: TopLevel?,
    setCurrentState: (TopLevel) -> Unit,
    navBackStack: NavBackStack<Destination>,
    modifier: Modifier = Modifier
) {

    val windowInsets = if (isEdgeToEdge) {
        WindowInsets(0, 0, 0, 0)
    } else {
        WindowInsets.safeDrawing
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = windowInsets,
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
        GlowBackground {
            Navigation(
                navBackStack = navBackStack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            )
        }
    }
}