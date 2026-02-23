package com.zavgar.system.authorization.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.authorization.ui.LoginScreen
import com.zavgar.system.designsystem.animation.FadeInTransition
import com.zavgar.system.designsystem.animation.FadeOutTransition
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.event.AuthEvent
import com.zavgar.system.navigationapi.provider.NavGraph

internal class LoginNavGraph : NavGraph {

    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit =
        { navEventController ->

            entry<AuthDestination.Login>(
                metadata = NavDisplay.transitionSpec { FadeInTransition } +
                        NavDisplay.popTransitionSpec { FadeOutTransition } +
                        NavDisplay.predictivePopTransitionSpec { FadeOutTransition }
            ) {
                LoginScreen(
                    onNavigateToWallet = {
                        navEventController.sendEvent(AuthEvent.LoginSubmit)
                    },
                    onNavigateToRegister = {
                        navEventController.sendEvent(AuthEvent.ToRegistration)
                    },
                    onNavigateToForgotPassword = {
                        navEventController.sendEvent(AuthEvent.ToPasswordRecovery)
                    }
                )
            }
        }
}