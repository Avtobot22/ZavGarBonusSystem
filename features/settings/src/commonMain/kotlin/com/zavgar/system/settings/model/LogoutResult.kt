package com.zavgar.system.settings.model

import com.zavgar.system.core.presentation.util.UiText

sealed interface LogoutResult {

    data object Success : LogoutResult

    data class Error(val message: UiText) : LogoutResult
}