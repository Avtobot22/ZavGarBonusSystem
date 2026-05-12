package com.zavgar.system.domain.userinfo.error

import com.zavgar.system.utils.result.AppError

sealed interface ChangePasswordError {
    data object ValidationError : ChangePasswordError
    data object TooManyRequestError : ChangePasswordError, AppError.TooManyRequest
    data object ServerError : ChangePasswordError, AppError.Server
    data object NetworkError : ChangePasswordError, AppError.Network
    data class UnknownError(override val message: String) : ChangePasswordError, AppError.Unknown
}
