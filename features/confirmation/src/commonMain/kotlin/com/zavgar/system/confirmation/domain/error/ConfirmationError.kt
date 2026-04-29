package com.zavgar.system.confirmation.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface ConfirmationError {
    data object InvalidCodeError : ConfirmationError
    data object TooManyRequestError : ConfirmationError
    data object ServerError : ConfirmationError
    data object NetworkError : ConfirmationError
    data class UnknownError(val message: String) : ConfirmationError
}

fun Throwable.toConfirmationError(): ConfirmationError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        400 -> ConfirmationError.InvalidCodeError
        429 -> ConfirmationError.TooManyRequestError
        else -> ConfirmationError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> ConfirmationError.ServerError
    is NetworkErrorKind.Network -> ConfirmationError.NetworkError
    is NetworkErrorKind.Unknown -> ConfirmationError.UnknownError(kind.message)
}
