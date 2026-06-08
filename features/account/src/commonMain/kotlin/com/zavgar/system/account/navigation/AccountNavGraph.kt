package com.zavgar.system.account.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.account.ui.AccountScreen
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.SettingsDestination
import com.zavgar.system.navigationapi.event.ProfileEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.navigationapi.transition.MainEnterTransition
import com.zavgar.system.navigationapi.transition.MainPopTransition

class AccountNavGraph : NavGraph {
    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit = { navEventController ->

        entry<SettingsDestination.Profile>(
            metadata = NavDisplay.transitionSpec { MainEnterTransition } +
                NavDisplay.popTransitionSpec { MainPopTransition } +
                NavDisplay.predictivePopTransitionSpec { MainPopTransition },
        ) {
            AccountScreen(
                onNavigateToLogin = {
                    navEventController.sendEvent(ProfileEvent.DeleteAccount)
                },
                onNavigateBack = {
                    navEventController.sendEvent(ProfileEvent.Back)
                },
            )
        }
    }
}
