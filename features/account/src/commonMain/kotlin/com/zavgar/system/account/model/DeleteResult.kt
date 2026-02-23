package com.zavgar.system.account.model

import com.zavgar.system.core.presentation.util.UiText

sealed interface DeleteResult {
    data object Success : DeleteResult
    data class Error(val message: UiText) : DeleteResult
    data object TokenExpired : DeleteResult
}