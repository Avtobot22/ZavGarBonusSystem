package com.zavgar.system.account.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface ProfileUpdateResult {
    data object Success : ProfileUpdateResult
    data class Error(val message: SnackBarMessage) : ProfileUpdateResult

    data object TokenExpired : ProfileUpdateResult
}