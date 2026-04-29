package com.zavgar.system.history.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface OperationsError {
    data object ValidationError : OperationsError
    data object UserNotFound : OperationsError
    data object TooManyRequestError : OperationsError
    data object ServerError : OperationsError
    data object NetworkError : OperationsError
    data class UnknownError(val message: String) : OperationsError
}

fun Throwable.toOperationsError(): OperationsError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        400 -> OperationsError.ValidationError
        404 -> OperationsError.UserNotFound
        429 -> OperationsError.TooManyRequestError
        else -> OperationsError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> OperationsError.ServerError
    is NetworkErrorKind.Network -> OperationsError.NetworkError
    is NetworkErrorKind.Unknown -> OperationsError.UnknownError(kind.message)
}
