package com.zavgar.system.registration.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface RegisterResult {
    data object Success : RegisterResult
    data class Error(val message: SnackBarMessage) : RegisterResult
}