package com.zavgar.system.splash.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.zavgar.system.navigationapi.transition.AuthEnterTransition
import com.zavgar.system.navigationapi.transition.AuthPopTransition
import com.zavgar.system.navigationapi.controller.NavEventController
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.SplashDestination
import com.zavgar.system.navigationapi.event.SplashEvent
import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.splash.ui.SplashScreen

class SplashNavGraph : NavGraph {
    override val navGraph: EntryProviderScope<Destination>.(NavEventController) -> Unit = { navController ->
        entry<SplashDestination>(
            metadata = NavDisplay.transitionSpec { AuthEnterTransition } +
                    NavDisplay.popTransitionSpec { AuthPopTransition } +
                    NavDisplay.predictivePopTransitionSpec { AuthPopTransition }
        ) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.sendEvent(SplashEvent.NavigateToLogin)
                },
                onNavigateToWallet = {
                    navController.sendEvent(SplashEvent.NavigateToWallet)
                },
                onNavigateToOnboarding = {
                    navController.sendEvent(SplashEvent.NavigateToOnboarding)
                }
            )
        }
    }
}