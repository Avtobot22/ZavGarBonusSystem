package com.zavgar.system.authorization.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface LoginEvent {

    data class NavigateToConfirmation(val phone: String) : LoginEvent

    data object NavigateToRegister : LoginEvent

    data class ShowSnackbar(val message: SnackBarMessage) : LoginEvent
}
