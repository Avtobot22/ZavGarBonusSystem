package com.zavgar.system.confirmation.presentation

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.utils.validation.CODE_LENGTH

data class ConfirmationState(
    val phone: String = "",

    val isRegistration: Boolean = false,

    val code: String = "",

    val codeError: UiText? = null,

    val screenState: ScreenState = ScreenState.Idle,

    val timerSeconds: Int = 0
) {
    sealed interface ScreenState {
        data object Idle : ScreenState
        data object Submitting : ScreenState
    }

    val isCodeValid: Boolean
        get() = code.length == CODE_LENGTH

    val isConfirmButtonEnabled: Boolean
        get() = isCodeValid && screenState is ScreenState.Idle
}
