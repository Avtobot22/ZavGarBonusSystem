package com.zavgar.system.navigation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.navigation.analytics.screenName
import com.zavgar.system.navigation.provider.NavGraphProvider
import com.zavgar.system.navigationapi.controller.NavBackStack
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.event.ClearAndNavigate
import com.zavgar.system.navigationapi.event.ClearAndNavigateToTopLevel
import com.zavgar.system.navigationapi.event.Event
import com.zavgar.system.navigationapi.event.ReplaceNavigation
import com.zavgar.system.navigationapi.marker.TopLevel
import org.koin.compose.koinInject

@Composable
fun Navigation(
    navBackStack: NavBackStack<Destination>,
    modifier: Modifier = Modifier,
) {
    NavigationLoader(
        navBackStack = navBackStack,
        modifier = modifier,
    )
}

@Composable
private fun NavigationLoader(
    navBackStack: NavBackStack<Destination>,
    modifier: Modifier = Modifier,
    navEventController: NavEventController = koinInject(),
    navGraphProvider: NavGraphProvider = koinInject(),
    analyticsTracker: AnalyticsTracker = koinInject(),
) {
    val dialogStrategy = remember { DialogSceneStrategy<Destination>() }

    LaunchedEffect(navEventController) {
        navEventController.eventState.collect { event ->
            handleNavEvent(event, navBackStack)
        }
    }

    LaunchedEffect(navBackStack, analyticsTracker) {
        snapshotFlow { navBackStack.backStack.lastOrNull()?.screenName() }
            .collect { screenName -> screenName?.let(analyticsTracker::logScreenView) }
    }

    NavDisplay(
        backStack = navBackStack.backStack,
        onBack = { navBackStack.removeLast() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        sceneStrategy = dialogStrategy,
        entryProvider = navGraphProvider.navigationGraph,
        modifier = modifier,
    )
}

private fun handleNavEvent(
    event: Event,
    navBackStack: NavBackStack<Destination>,
) {
    val destination = event.nextDestination()
    when {
        destination is Destination.Back -> navBackStack.removeLast()
        event is ClearAndNavigateToTopLevel -> navBackStack.clearAndNavigateToTopLevel(destination)
        event is ClearAndNavigate -> navBackStack.clearAndNavigate(destination)
        event is ReplaceNavigation -> navBackStack.replaceTop(destination)
        destination is TopLevel -> {
            if (destination == navBackStack.topLevelKey) {
                navBackStack.clearTopLevel(destination)
            } else {
                navBackStack.addTopLevel(destination)
            }
        }
        else -> navBackStack.add(destination)
    }
}
