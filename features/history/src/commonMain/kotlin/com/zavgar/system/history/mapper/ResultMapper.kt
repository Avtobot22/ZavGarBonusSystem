package com.zavgar.system.history.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.mapAppError
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.domain.operations.error.OperationsError
import com.zavgar.system.domain.operations.model.TransactionsPageResponse
import com.zavgar.system.history.model.TransactionsResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_user_not_found

fun OperationsError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is OperationsError.ValidationError -> SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_format))
    is OperationsError.UserNotFound -> SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found))
    is AppError -> mapAppError(this)
}

fun AppResult<TransactionsPageResponse, OperationsError>.toTransactionsResult(): TransactionsResult =
    when (this) {
        is AppResult.Success -> TransactionsResult.Success(data)
        is AppResult.Error -> TransactionsResult.Error(error.asSnackBarMessage())
    }
