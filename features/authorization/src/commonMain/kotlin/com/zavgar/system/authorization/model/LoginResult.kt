package com.zavgar.system.authorization.model

import com.zavgar.system.core.presentation.util.UiText

sealed interface LoginResult {
    data object Success : LoginResult
    data class Error(val message: UiText) : LoginResult
}