package com.zavgar.system.history.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
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
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_user_not_found

fun OperationsError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is OperationsError.ValidationError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_invalid_format),
        type = SnackBarType.WARNING
    )

    is OperationsError.NotAuthorizedError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
        type = SnackBarType.WARNING
    )

    is OperationsError.UserNotFound -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
        type = SnackBarType.WARNING
    )

    is OperationsError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is OperationsError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is OperationsError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is OperationsError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}

fun AppResult<TransactionsPageResponse, OperationsError>.toTransactionsResult(
    dataMapper: (TransactionsPageResponse) -> History,
): TransactionsResult = when (this) {
    is AppResult.Success -> TransactionsResult.Success(dataMapper(data))
    is AppResult.Error -> TransactionsResult.Error(error.asSnackBarMessage())
}
