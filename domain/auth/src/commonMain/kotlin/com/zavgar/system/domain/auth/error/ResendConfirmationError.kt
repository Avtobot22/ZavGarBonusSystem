package com.zavgar.system.domain.auth.error

import com.zavgar.system.utils.result.AppError

sealed interface ResendConfirmationError {
    data object InvalidPhone : ResendConfirmationError
    data object TooManyRequestError : ResendConfirmationError, AppError.TooManyRequest
    data object ServerError : ResendConfirmationError, AppError.Server
    data object NetworkError : ResendConfirmationError, AppError.Network
    data class UnknownError(override val message: String) : ResendConfirmationError, AppError.Unknown
}
