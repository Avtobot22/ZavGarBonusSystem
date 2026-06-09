package com.zavgar.system.domain.auth.error

import com.zavgar.system.utils.result.AppError

sealed interface AuthError {
    data object ValidationError : AuthError
    data object UserNotFound : AuthError
    data class TooManyRequestError(override val retryAfterSeconds: Long? = null) : AuthError, AppError.TooManyRequest
    data object ServerError : AuthError, AppError.Server
    data object NetworkError : AuthError, AppError.Network
    data class UnknownError(override val message: String) : AuthError, AppError.Unknown
}
