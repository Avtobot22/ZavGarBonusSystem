package com.zavgar.system.account.presentation

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.designsystem.components.snackbar.SnackBarType

sealed interface AccountEvent {

    data object NavigateToLogin : AccountEvent

    data object NavigateBack : AccountEvent

    data class ShowSnackbar(val message: UiText, val type: SnackBarType) : AccountEvent

}