package com.zavgar.system.history.model

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.domain.operations.model.TransactionsPageResponse

sealed interface TransactionsResult {
    data class Success(val page: TransactionsPageResponse) : TransactionsResult
    data class Error(val message: SnackBarMessage) : TransactionsResult
}
