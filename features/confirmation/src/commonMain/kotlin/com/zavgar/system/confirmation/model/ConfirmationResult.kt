package com.zavgar.system.confirmation.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface ConfirmationResult {
    data object Success : ConfirmationResult
    data class Error(val message: SnackBarMessage) : ConfirmationResult
}