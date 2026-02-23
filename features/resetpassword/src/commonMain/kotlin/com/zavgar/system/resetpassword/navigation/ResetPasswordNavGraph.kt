package com.zavgar.system.resetpassword.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.designsystem.animation.FadeInTransition
import com.zavgar.system.designsystem.animation.FadeOutTransition
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.event.AuthEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.resetpassword.ui.ResetPasswordScreen

class ResetPasswordNavGraph : NavGraph {
    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit = { navGraphController ->
        entry<AuthDestination.Reset>(
            metadata = NavDisplay.transitionSpec { FadeInTransition } +
                    NavDisplay.popTransitionSpec { FadeOutTransition } +
                    NavDisplay.predictivePopTransitionSpec { FadeOutTransition }
        ) {
            ResetPasswordScreen(
                onNavigateToConfirm = { phone ->
                    navGraphController.sendEvent(AuthEvent.ResetSubmit(phone))
                },

                onNavigateToLogin = {
                    navGraphController.sendEvent(AuthEvent.ToLogin)
                }
            )
        }
    }
}