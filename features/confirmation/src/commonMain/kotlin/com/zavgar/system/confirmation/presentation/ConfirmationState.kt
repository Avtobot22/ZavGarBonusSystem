package com.zavgar.system.confirmation.presentation

import com.zavgar.system.core.presentation.util.UiText

data class ConfirmationState(
    val phone: String = "",

    val isRegistration: Boolean = false,

    val code: String = "",

    val codeError: UiText? = null,

    val isLoading: Boolean = false,

    val timerSeconds: Int = 0
) {
    val isCodeValid: Boolean
        get() = code.length == 6

    val isConfirmButtonEnabled: Boolean
        get() = isCodeValid && !isLoading
}
