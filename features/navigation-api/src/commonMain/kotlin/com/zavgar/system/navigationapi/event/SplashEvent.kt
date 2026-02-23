package com.zavgar.system.navigationapi.event

import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.HomeDestination

/**
 * Events for Splash screen navigation decisions.
 */
object SplashEvent {

    /**
     * Navigate to Login screen when user is not authenticated.
     * Replaces Splash with Login.
     */
    data object NavigateToLogin : Event, ReplaceNavigation {
        override fun nextDestination(): Destination = AuthDestination.Login
    }

    /**
     * Navigate to Wallet (main app) when user is authenticated.
     * Replaces Splash with Wallet (TopLevel).
     */
    data object NavigateToWallet : Event, ReplaceNavigation, ClearAndNavigateToTopLevel {
        override fun nextDestination(): Destination = HomeDestination.Wallet
    }
}
