package com.zavgar.system.navigationapi.event

import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination

/**
 * Events for Onboarding screen navigation.
 */
object OnboardingEvent {

    /**
     * Triggered when the user finishes or skips onboarding.
     * Replaces Onboarding with Login.
     */
    data object Complete : Event, ReplaceNavigation {
        override fun nextDestination(): Destination = AuthDestination.Login
    }
}
