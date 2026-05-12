package com.zavgar.system.domain.userinfo.error

import com.zavgar.system.utils.result.AppError

sealed interface LogoutError {
    data object TooManyRequestError : LogoutError, AppError.TooManyRequest
    data object ServerError : LogoutError, AppError.Server
    data object NetworkError : LogoutError, AppError.Network
    data class UnknownError(override val message: String) : LogoutError, AppError.Unknown
}
