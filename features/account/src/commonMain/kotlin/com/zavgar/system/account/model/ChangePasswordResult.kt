package com.zavgar.system.account.model

import com.zavgar.system.core.presentation.util.UiText

sealed interface ChangePasswordResult {
    data object Success : ChangePasswordResult
    data class Error(val message: UiText) : ChangePasswordResult
    data object TokenExpired : ChangePasswordResult
}