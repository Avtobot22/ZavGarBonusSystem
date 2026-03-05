package com.zavgar.system.account.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface DeleteResult {
    data object Success : DeleteResult
    data class Error(val message: SnackBarMessage) : DeleteResult
    data object TokenExpired : DeleteResult
}