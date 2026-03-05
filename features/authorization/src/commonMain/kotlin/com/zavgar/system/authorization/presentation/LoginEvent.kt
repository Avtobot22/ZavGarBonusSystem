package com.zavgar.system.authorization.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface LoginEvent {

    data object NavigateToWallet : LoginEvent

    data object NavigateToRegister : LoginEvent

    data object NavigateToForgotPassword : LoginEvent

    data class ShowSnackbar(val message: SnackBarMessage) : LoginEvent
}