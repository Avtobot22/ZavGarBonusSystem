package com.zavgar.system.history.model

import com.zavgar.system.core.presentation.util.UiText

sealed interface TransactionsResult {
    data class Success(val history: History) : TransactionsResult
    data class Error(val message: UiText) : TransactionsResult
    data object TokenExpired : TransactionsResult
}