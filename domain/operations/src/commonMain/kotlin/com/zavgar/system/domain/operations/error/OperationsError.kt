package com.zavgar.system.domain.operations.error

import com.zavgar.system.utils.result.AppError

sealed interface OperationsError {
    data object ValidationError : OperationsError
    data object UserNotFound : OperationsError
    data class TooManyRequestError(
        override val retryAfterSeconds: Long? = null,
    ) : OperationsError, AppError.TooManyRequest
    data object ServerError : OperationsError, AppError.Server
    data object NetworkError : OperationsError, AppError.Network
    data class UnknownError(override val message: String) : OperationsError, AppError.Unknown
}
