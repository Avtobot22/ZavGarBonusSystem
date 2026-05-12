package com.zavgar.system.domain.auth.error

import com.zavgar.system.utils.result.AppError

sealed interface RegisterError {
    data object InvalidFormat : RegisterError
    data object UserAlreadyExists : RegisterError
    data object TooManyRequestError : RegisterError, AppError.TooManyRequest
    data object NetworkError : RegisterError, AppError.Network
    data object ServerError : RegisterError, AppError.Server
    data class UnknownError(override val message: String) : RegisterError, AppError.Unknown
}
