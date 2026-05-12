package com.zavgar.system.domain.auth.error

import com.zavgar.system.utils.result.AppError

sealed interface ResetPasswordError {
    data object InvalidPhoneError : ResetPasswordError
    data object UserNotFound : ResetPasswordError
    data object TooManyRequestError : ResetPasswordError, AppError.TooManyRequest
    data object ServerError : ResetPasswordError, AppError.Server
    data object NetworkError : ResetPasswordError, AppError.Network
    data class UnknownError(override val message: String) : ResetPasswordError, AppError.Unknown
}
