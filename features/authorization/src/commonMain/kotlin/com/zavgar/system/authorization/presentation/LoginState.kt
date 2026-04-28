package com.zavgar.system.authorization.presentation

import com.zavgar.system.core.presentation.util.UiText

data class LoginState(
    val screenState: ScreenState = ScreenState.Idle,
    val phone: String = "",
    val password: String = "",
    val phoneError: UiText? = null,
    val passwordError: UiText? = null,
    val isPhoneValid: Boolean = false,
    val generalError: UiText? = null,
) {
    sealed interface ScreenState {
        data object Idle : ScreenState
        data object Submitting : ScreenState
    }

    val isFormFilled: Boolean
        get() = phone.isNotBlank() && password.isNotBlank()

    val isLoginButtonEnabled: Boolean
        get() = isFormFilled && screenState is ScreenState.Idle
}
