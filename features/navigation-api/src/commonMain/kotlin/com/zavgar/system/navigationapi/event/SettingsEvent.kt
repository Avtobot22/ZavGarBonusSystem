package com.zavgar.system.navigationapi.event

import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.SettingsDestination

/**
 * Defines navigation events triggered from the App Settings screen.
 */
object SettingsEvent {

    /**
     * Triggered when the user clicks on the "My Profile" card in Settings.
     * * Navigates to the detailed profile information.
     */
    data object ToProfileDetail : Event {
        override fun nextDestination(): Destination = SettingsDestination.Profile
    }

    /**
     * Triggered when the user confirms logging out.
     *
     * The Navigator should handle clearing the backstack so the user cannot
     * go back to the Wallet without logging in again.
     */
    data object Logout : Event, ClearAndNavigate {
        override fun nextDestination(): Destination = AuthDestination.Login
    }
}