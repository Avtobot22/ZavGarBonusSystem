package com.zavgar.system.wallet.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.wallet.model.BalanceResult

fun Balance.toPresentation() = this.balance

fun GetBalanceError.asSnackBarMessage() = when (this) {
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

fun AppResult<Balance, GetBalanceError>.toBalanceResult(): BalanceResult = when (this) {
    is AppResult.Success -> BalanceResult.Success(data.toPresentation())
    is AppResult.Error -> BalanceResult.Error(error.asSnackBarMessage())
}
