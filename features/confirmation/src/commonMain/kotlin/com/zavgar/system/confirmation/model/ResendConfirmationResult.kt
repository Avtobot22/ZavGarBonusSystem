package com.zavgar.system.confirmation.model

import com.zavgar.system.core.presentation.util.UiText

sealed interface ResendConfirmationResult {
    data object Success : ResendConfirmationResult
    data class Error(val message: UiText) : ResendConfirmationResult
}