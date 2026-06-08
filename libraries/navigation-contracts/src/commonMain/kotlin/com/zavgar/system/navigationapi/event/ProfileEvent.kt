package com.zavgar.system.navigationapi.event

import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination

/**
 * Defines navigation events related to the User Profile and Account Management.
 */
object ProfileEvent {

    // --- Account Actions (Destructive/Exit) ---

    /**
     * Triggered when the user confirms account deletion.
     *
     * Similar to Logout, this should redirect to the Login screen
     * after the API successfully deletes the data.
     */
    data object DeleteAccount : Event, ClearAndNavigate {
        override fun nextDestination(): Destination = AuthDestination.Login
    }

    data object Back : Event {
        override fun nextDestination(): Destination = Destination.Back
    }
}
