package com.zavgar.system.registration.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface RegisterEvent {

    data class NavigateToConfirm(val phone: String) : RegisterEvent

    data object NavigateToLogin : RegisterEvent

    data class ShowSnackbar(val message: SnackBarMessage) : RegisterEvent
}
