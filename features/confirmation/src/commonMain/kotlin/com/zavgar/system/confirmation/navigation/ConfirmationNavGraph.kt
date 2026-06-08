package com.zavgar.system.confirmation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.confirmation.ui.ConfirmationScreen
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.event.AuthEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.navigationapi.transition.AuthEnterTransition
import com.zavgar.system.navigationapi.transition.AuthPopTransition

class ConfirmationNavGraph : NavGraph {
    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit =
        { navGraphController ->

            entry<AuthDestination.Confirmation>(
                metadata = NavDisplay.transitionSpec { AuthEnterTransition } +
                    NavDisplay.popTransitionSpec { AuthPopTransition } +
                    NavDisplay.predictivePopTransitionSpec { AuthPopTransition },
            ) { entry ->
                ConfirmationScreen(
                    phone = entry.phone,
                    isRegistration = entry.isRegistration,
                    onNavigateToLogin = {
                        navGraphController.sendEvent(AuthEvent.ToLogin)
                    },
                    onNavigateToWallet = {
                        navGraphController.sendEvent(AuthEvent.ConfirmLoginSuccess)
                    },
                )
            }
        }
}
