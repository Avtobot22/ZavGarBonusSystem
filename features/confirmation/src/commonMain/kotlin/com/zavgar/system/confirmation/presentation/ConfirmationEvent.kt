package com.zavgar.system.confirmation.presentation

import com.zavgar.system.core.presentation.util.UiText

sealed interface ConfirmationEvent {

    data object NavigateToLogin : ConfirmationEvent

    data class ShowSnackbar(val message: UiText) : ConfirmationEvent
}