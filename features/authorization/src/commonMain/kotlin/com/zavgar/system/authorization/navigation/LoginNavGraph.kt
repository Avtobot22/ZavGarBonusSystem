package com.zavgar.system.authorization.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.authorization.ui.LoginScreen
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.event.AuthEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.navigationapi.transition.AuthEnterTransition
import com.zavgar.system.navigationapi.transition.AuthPopTransition

internal class LoginNavGraph : NavGraph {

    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit =
        { navEventController ->

            entry<AuthDestination.Login>(
                metadata = NavDisplay.transitionSpec { AuthEnterTransition } +
                    NavDisplay.popTransitionSpec { AuthPopTransition } +
                    NavDisplay.predictivePopTransitionSpec { AuthPopTransition },
            ) {
                LoginScreen(
                    onNavigateToConfirmation = { phone ->
                        navEventController.sendEvent(AuthEvent.LoginOtpSent(phone))
                    },
                    onNavigateToRegister = {
                        navEventController.sendEvent(AuthEvent.ToRegistration)
                    },
                )
            }
        }
}
