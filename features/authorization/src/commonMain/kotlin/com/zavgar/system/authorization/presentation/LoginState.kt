package com.zavgar.system.authorization.presentation

import com.zavgar.system.core.presentation.util.UiText

data class LoginState(
    val phone: String = "",

    val password: String = "",

    val phoneError: UiText? = null,

    val passwordError: UiText? = null,

    val isPhoneValid: Boolean = false,

    val isLoading: Boolean = false,

    val generalError: UiText? = null
)