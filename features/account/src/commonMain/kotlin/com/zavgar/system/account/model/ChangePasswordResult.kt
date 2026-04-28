package com.zavgar.system.account.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface ChangePasswordResult {
    data object Success : ChangePasswordResult
    data class Error(val message: SnackBarMessage) : ChangePasswordResult
}
