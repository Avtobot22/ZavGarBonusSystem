package com.zavgar.system.confirmation.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface ResendConfirmationResult {
    data object Success : ResendConfirmationResult
    data class Error(val message: SnackBarMessage) : ResendConfirmationResult
}