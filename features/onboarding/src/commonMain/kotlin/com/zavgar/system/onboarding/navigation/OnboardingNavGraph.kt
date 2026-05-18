package com.zavgar.system.onboarding.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.navigationapi.transition.AuthEnterTransition
import com.zavgar.system.navigationapi.transition.AuthPopTransition
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.OnboardingDestination
import com.zavgar.system.navigationapi.event.OnboardingEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.onboarding.ui.OnboardingScreen

class OnboardingNavGraph : NavGraph {
    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit = { navController ->
        entry<OnboardingDestination>(
            metadata = NavDisplay.transitionSpec { AuthEnterTransition } +
                    NavDisplay.popTransitionSpec { AuthPopTransition } +
                    NavDisplay.predictivePopTransitionSpec { AuthPopTransition }
        ) {
            OnboardingScreen(
                onNavigateToLogin = {
                    navController.sendEvent(OnboardingEvent.Complete)
                }
            )
        }
    }
}
