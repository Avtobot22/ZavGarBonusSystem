package com.zavgar.system.account.presentation

import com.zavgar.system.core.presentation.util.UiText

sealed interface AccountEvent {

    data object NavigateToLogin : AccountEvent

    data object NavigateBack : AccountEvent

    data class ShowSnackbar(val message: UiText) : AccountEvent

}