package com.zavgar.system.settings.model

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface LogoutResult {

    data object Success : LogoutResult

    data class Error(val message: SnackBarMessage) : LogoutResult
}
