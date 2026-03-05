package com.zavgar.system.resetpassword.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface ResetPasswordEvent {

    data class NavigateToConfirm(val phone: String) : ResetPasswordEvent

    data object NavigateToLogin : ResetPasswordEvent

    data class ShowSnackbar(val message: SnackBarMessage) : ResetPasswordEvent
}