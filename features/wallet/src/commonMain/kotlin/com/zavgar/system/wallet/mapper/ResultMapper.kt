package com.zavgar.system.wallet.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.GetBalanceError
import com.zavgar.system.domain.model.response.Balance
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.wallet.model.BalanceResult

fun <T, E> AppResult<T, E>.toBalanceResult(
    errorMapper: (E) -> SnackBarMessage,
    dataMapper: (T) -> Int,
    isTokenExpired: (E) -> Boolean
): BalanceResult {
    return when (this) {
        is AppResult.Success -> BalanceResult.Success(dataMapper(data))
        is AppResult.Error -> if (isTokenExpired(this.error)) {
            BalanceResult.TokenExpired
        } else {
            BalanceResult.Error(errorMapper(this.error))
        }
    }
}

fun Balance.toPresentation() = this.balance

fun GetBalanceError.asSnackBarMessage() = when (this) {
    is GetBalanceError.NotAuthorizedError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
        type = SnackBarType.WARNING
    )

    is GetBalanceError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is GetBalanceError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is GetBalanceError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is GetBalanceError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}

fun AppResult<Balance, GetBalanceError>.toBalanceResult() =
    toBalanceResult(
        GetBalanceError::asSnackBarMessage,
        Balance::toPresentation
    ) { it is GetBalanceError.NotAuthorizedError }
