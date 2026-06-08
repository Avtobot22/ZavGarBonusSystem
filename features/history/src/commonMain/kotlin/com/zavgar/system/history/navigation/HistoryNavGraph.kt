package com.zavgar.system.history.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.history.ui.HistoryScreen
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.HomeDestination
import com.zavgar.system.navigationapi.event.HomeEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.navigationapi.transition.MainEnterTransition
import com.zavgar.system.navigationapi.transition.MainPopTransition

class HistoryNavGraph : NavGraph {
    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit = { navEventController ->
        entry<HomeDestination.History>(
            metadata = NavDisplay.transitionSpec { MainEnterTransition } +
                NavDisplay.popTransitionSpec { MainPopTransition } +
                NavDisplay.predictivePopTransitionSpec { MainPopTransition },
        ) {
            HistoryScreen(
                onNavigateToLogin = {
                    navEventController.sendEvent(HomeEvent.ToLogin)
                },
            )
        }
    }
}
