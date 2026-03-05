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

    val isLoading: Boolean = false,
) {
    val isFormFilled: Boolean
        get() = phone.isNotBlank() &&
                password.isNotBlank() &&
                repeatPassword.isNotBlank()

    val isResetPasswordButtonEnabled: Boolean
        get() = isFormFilled && !isLoading
}
