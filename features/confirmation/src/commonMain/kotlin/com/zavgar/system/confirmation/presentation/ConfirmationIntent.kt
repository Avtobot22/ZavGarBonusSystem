package com.zavgar.system.confirmation.presentation

sealed interface ConfirmationIntent {

    data class Initialize(val phone: String, val isRegistration: Boolean) : ConfirmationIntent
    data class EnterCode(val code: String) : ConfirmationIntent

    data object ClickResend : ConfirmationIntent

    data object Submit : ConfirmationIntent
}
