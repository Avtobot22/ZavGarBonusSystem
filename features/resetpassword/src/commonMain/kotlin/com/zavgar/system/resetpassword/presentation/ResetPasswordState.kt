package com.zavgar.system.resetpassword.presentation

import com.zavgar.system.core.presentation.util.UiText

data class ResetPasswordState(
    val phone: String = "",
    val password: String = "",
    val repeatPassword: String = "",

    val isPhoneValid: Boolean = false,
    val phoneError: UiText? = null,
    val passwordError: UiText? = null,
    val repeatPasswordError: UiText? = null,

    val screenState: ScreenState = ScreenState.Idle,
) {
    sealed interface ScreenState {
        data object Idle : ScreenState
        data object Submitting : ScreenState
    }

    val isFormFilled: Boolean
        get() = phone.isNotBlank() &&
                password.isNotBlank() &&
                repeatPassword.isNotBlank()

    val isResetPasswordButtonEnabled: Boolean
        get() = isFormFilled && screenState is ScreenState.Idle
}
