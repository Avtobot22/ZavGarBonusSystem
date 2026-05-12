package com.zavgar.system.domain.auth.error

import com.zavgar.system.utils.result.AppError

sealed interface ConfirmationError {
    data object InvalidCodeError : ConfirmationError
    data object TooManyRequestError : ConfirmationError, AppError.TooManyRequest
    data object ServerError : ConfirmationError, AppError.Server
    data object NetworkError : ConfirmationError, AppError.Network
    data class UnknownError(override val message: String) : ConfirmationError, AppError.Unknown
}
