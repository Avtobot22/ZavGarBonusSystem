package com.zavgar.system.domain.userinfo.error

import com.zavgar.system.utils.result.AppError

sealed interface GetBalanceError {
    data object TooManyRequestError : GetBalanceError, AppError.TooManyRequest
    data object ServerError : GetBalanceError, AppError.Server
    data object NetworkError : GetBalanceError, AppError.Network
    data class UnknownError(override val message: String) : GetBalanceError, AppError.Unknown
}
