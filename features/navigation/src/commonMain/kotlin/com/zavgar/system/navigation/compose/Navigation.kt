package com.zavgar.system.navigation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.navigation.provider.NavGraphProvider
import com.zavgar.system.navigationapi.controller.NavBackStack
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.event.ClearAndNavigate
import com.zavgar.system.navigationapi.event.ClearAndNavigateToTopLevel
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
) {

    val dialogStrategy = remember { DialogSceneStrategy<Destination>() }

    LaunchedEffect(Unit) {
        navEventController.eventState.collect { event ->
            val destination = event.nextDestination()

            when {
                // Back navigation
                destination is Destination.Back -> {
                    navBackStack.removeLast()
                }

                // Clear all and navigate to TopLevel (Auth success → Wallet)
                event is ClearAndNavigateToTopLevel -> {
                    navBackStack.clearAndNavigateToTopLevel(destination)
                }

                // Clear all and navigate (Logout → Login)
                event is ClearAndNavigate -> {
                    navBackStack.clearAndNavigate(destination)
                }

                // Replace current destination (Splash → Login/Wallet)
                event is ReplaceNavigation -> {
                    navBackStack.replaceTop(destination)
                }

                // TopLevel tab switching
                destination is TopLevel -> {
                    if (destination == navBackStack.topLevelKey) {
                        navBackStack.clearTopLevel(destination)
                    } else {
                        navBackStack.addTopLevel(destination)
                    }
                }

                // Regular navigation
                else -> {
                    navBackStack.add(destination)
                }
            }
        }
    }

    NavDisplay(
        backStack = navBackStack.backStack,
        onBack = { navBackStack.removeLast() },
        sceneStrategy = dialogStrategy,
        entryProvider = navGraphProvider.navigationGraph,
        modifier = modifier,
    )
}
