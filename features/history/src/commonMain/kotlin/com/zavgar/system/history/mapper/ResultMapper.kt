package com.zavgar.system.history.mapper

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.OperationsError
import com.zavgar.system.domain.model.response.TransactionsPageResponse
import com.zavgar.system.history.model.History
import com.zavgar.system.history.model.TransactionsResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_user_not_found

fun <T, E> AppResult<T, E>.toTransactionsResult(
    errorMapper: (E) -> UiText,
    isTokenExpired: (E) -> Boolean,
    dataMapper: (T) -> History,
): TransactionsResult {
    return when (this) {
        is AppResult.Success -> TransactionsResult.Success(dataMapper(this.data))
        is AppResult.Error -> if (isTokenExpired(this.error)) {
            TransactionsResult.TokenExpired
        } else {
            TransactionsResult.Error(errorMapper(this.error))
        }
    }
}

fun OperationsError.asUiText(): UiText = when (this) {
    is OperationsError.ValidationError -> UiText.Resource(Res.string.error_invalid_format)
    is OperationsError.NotAuthorizedError -> UiText.Resource(Res.string.error_user_not_found)
    is OperationsError.UserNotFound -> UiText.Resource(Res.string.error_user_not_found)
    is OperationsError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is OperationsError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is OperationsError.UnknownError -> UiText.DynamicString(this.message)
}

fun AppResult<TransactionsPageResponse, OperationsError>.toTransactionsResult(dataMapper: (TransactionsPageResponse) -> History) =
    toTransactionsResult(OperationsError::asUiText, { it is OperationsError.NotAuthorizedError }, dataMapper)