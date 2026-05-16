package com.zavgar.system.confirmation.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface ConfirmationEvent {

    data object NavigateToLogin : ConfirmationEvent

    data object NavigateToWallet : ConfirmationEvent

    data class ShowSnackbar(val message: SnackBarMessage) : ConfirmationEvent
}