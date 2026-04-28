package com.zavgar.system.navigationapi.event

import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.HomeDestination

/**
 * Defines all navigation events within the Authentication flow.
 *
 * This object groups user interactions (clicks) and system results (success/failure)
 * that trigger a screen transition.
 */
object AuthEvent {

    // --- Login Screen Events ---

    /**
     * Triggered when the user successfully logs in.
     *
     * Usually called after the ViewModel receives a success response from the server.
     * Navigates to the main application screen (Wallet).
     */
    data object LoginSubmit : Event, ClearAndNavigateToTopLevel {
        override fun nextDestination(): Destination = HomeDestination.Wallet
    }

    /**
     * Triggered when the user clicks the "No account? Register" text link.
     *
     * Navigates the user from the Login screen to the Registration form.
     */
    data object ToRegistration : Event {
        override fun nextDestination(): Destination = AuthDestination.Register
    }

    /**
     * Triggered when the user clicks the "Forgot Password?" text link.
     *
     * Navigates to the Password Recovery flow.
     */
    data object ToPasswordRecovery : Event {
        override fun nextDestination(): Destination = AuthDestination.Reset
    }


    // --- Registration Screen Events ---

    /**
     * Triggered when the user clicks the primary "Register" button (Orange button).
     *
     * This event initiates the phone verification process.
     *
     * @param phone The phone number entered by the user. Passed to the next screen for API calls.
     */
    data class RegisterSubmit(val phone: String) : Event {
        override fun nextDestination(): Destination =
            AuthDestination.Confirmation(phone = phone, isRegistration = true)
    }

    /**
     * Triggered when the user clicks the "Already have an account? Login" link.
     *
     * Returns the user to the Login screen.
     */
    data object ToLogin : Event {
        override fun nextDestination(): Destination = AuthDestination.Login
    }


    // --- Password Recovery Screen Events ---

    /**
     * Triggered when the user clicks the "Reset Password" / "Continue" button.
     *
     * Initiates the flow to verify the phone number for password reset.
     *
     * @param phone The phone number associated with the account to be recovered.
     */
    data class ResetSubmit(val phone: String) : Event {
        override fun nextDestination(): Destination =
            AuthDestination.Confirmation(phone = phone, isRegistration = false)
    }


    // --- Confirmation Screen Events ---

    /**
     * Triggered when the SMS code is successfully verified.
     *
     * Whether it was a registration or a password reset, a successful confirmation
     * grants access to the main app.
     */
    data object ConfirmationSuccess : Event, ClearAndNavigateToTopLevel {
        override fun nextDestination(): Destination = HomeDestination.Wallet
    }
}