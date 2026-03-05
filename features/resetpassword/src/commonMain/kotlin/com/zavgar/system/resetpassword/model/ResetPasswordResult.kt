package com.zavgar.system.resetpassword.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface ResetPasswordResult {
    data object Success : ResetPasswordResult
    data class Error(val message: SnackBarMessage) : ResetPasswordResult
}