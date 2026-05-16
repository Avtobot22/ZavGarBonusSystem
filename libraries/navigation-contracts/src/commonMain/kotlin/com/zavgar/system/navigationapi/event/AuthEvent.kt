package com.zavgar.system.navigationapi.event

import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.HomeDestination

object AuthEvent {

    // --- Login Screen Events ---

    /**
     * Triggered after OTP is successfully requested on the Login screen.
     * Navigates to Confirmation screen for login OTP entry.
     */
    data class LoginOtpSent(val phone: String) : Event {
        override fun nextDestination(): Destination =
            AuthDestination.Confirmation(phone = phone, isRegistration = false)
    }

    /**
     * Triggered when the user clicks the "Register" link on Login.
     */
    data object ToRegistration : Event {
        override fun nextDestination(): Destination = AuthDestination.Register
    }


    // --- Registration Screen Events ---

    /**
     * Triggered when the user submits the Registration form.
     * Navigates to Confirmation screen for registration OTP entry.
     */
    data class RegisterSubmit(val phone: String) : Event {
        override fun nextDestination(): Destination =
            AuthDestination.Confirmation(phone = phone, isRegistration = true)
    }

    /**
     * Triggered when the user clicks "Already have an account? Login".
     */
    data object ToLogin : Event {
        override fun nextDestination(): Destination = AuthDestination.Login
    }


    // --- Confirmation Screen Events ---

    /**
     * Triggered when the login OTP is successfully verified.
     * Clears the back stack and navigates to the main screen.
     */
    data object ConfirmLoginSuccess : Event, ClearAndNavigateToTopLevel {
        override fun nextDestination(): Destination = HomeDestination.Wallet
    }
}
