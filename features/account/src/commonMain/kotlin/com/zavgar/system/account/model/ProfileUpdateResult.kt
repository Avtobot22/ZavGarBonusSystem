package com.zavgar.system.account.model

import com.zavgar.system.core.presentation.util.UiText

sealed interface ProfileUpdateResult {
    data object Success : ProfileUpdateResult
    data class Error(val message: UiText) : ProfileUpdateResult

    data object TokenExpired : ProfileUpdateResult
}