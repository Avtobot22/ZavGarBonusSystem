package com.zavgar.system.wallet.mapper

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.response.Balance
import com.zavgar.system.domain.model.error.GetBalanceError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.wallet.model.BalanceResult

fun <T, E> AppResult<T, E>.toBalanceResult(
    errorMapper: (E) -> UiText,
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

fun GetBalanceError.asUiText() = when (this) {
    is GetBalanceError.NotAuthorizedError -> UiText.Resource(Res.string.error_user_not_found)
    is GetBalanceError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is GetBalanceError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is GetBalanceError.UnknownError -> UiText.DynamicString(this.message)

}

fun AppResult<Balance, GetBalanceError>.toBalanceResult() =
    toBalanceResult(GetBalanceError::asUiText, Balance::toPresentation) { it is GetBalanceError.NotAuthorizedError }