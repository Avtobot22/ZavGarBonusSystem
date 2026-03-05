package com.zavgar.system.authorization.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface LoginResult {
    data object Success : LoginResult
    data class Error(val message: SnackBarMessage) : LoginResult
}