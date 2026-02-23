package com.zavgar.system.registration.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.designsystem.animation.FadeInTransition
import com.zavgar.system.designsystem.animation.FadeOutTransition
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.event.AuthEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.registration.ui.RegisterScreen

internal class RegisterNavGraph : NavGraph {
    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit =
        { navEventController ->
            entry<AuthDestination.Register>(
                metadata = NavDisplay.transitionSpec { FadeInTransition } +
                        NavDisplay.popTransitionSpec { FadeOutTransition } +
                        NavDisplay.predictivePopTransitionSpec { FadeOutTransition }
            ) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navEventController.sendEvent(AuthEvent.ToLogin)
                    },
                    onNavigateToConfirm = { phone ->
                        navEventController.sendEvent(AuthEvent.RegisterSubmit(phone))
                    }
                )
            }
        }
}