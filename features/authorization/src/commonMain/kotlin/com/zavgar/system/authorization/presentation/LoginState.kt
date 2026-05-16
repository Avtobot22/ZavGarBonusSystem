package com.zavgar.system.authorization.presentation

import com.zavgar.system.core.presentation.util.UiText

data class LoginState(
    val screenState: ScreenState = ScreenState.Idle,
    val phone: String = "",
    val phoneError: UiText? = null,
    val isPhoneValid: Boolean = false,
) {
    sealed interface ScreenState {
        data object Idle : ScreenState
        data object Submitting : ScreenState
    }

    val isLoginButtonEnabled: Boolean
        get() = phone.isNotBlank() && screenState is ScreenState.Idle
}
