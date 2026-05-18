package com.zavgar.system.settings.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.navigationapi.transition.MainEnterTransition
import com.zavgar.system.navigationapi.transition.MainPopTransition
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.HomeDestination
import com.zavgar.system.navigationapi.event.SettingsEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.settings.ui.SettingsScreen

class SettingsNavGraph : NavGraph {
    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit = { navEventController ->
        entry<HomeDestination.Settings>(
            metadata = NavDisplay.transitionSpec { MainEnterTransition } +
                    NavDisplay.popTransitionSpec { MainPopTransition } +
                    NavDisplay.predictivePopTransitionSpec { MainPopTransition }
        ) {
            SettingsScreen(
                onNavigateToLogin = {
                    navEventController.sendEvent(SettingsEvent.Logout)
                },
                onNavigateToProfileDetail = {
                    navEventController.sendEvent(SettingsEvent.ToProfileDetail)
                }
            )
        }
    }
}