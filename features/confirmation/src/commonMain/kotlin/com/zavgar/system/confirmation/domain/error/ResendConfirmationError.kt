package com.zavgar.system.confirmation.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface ResendConfirmationError {
    data object InvalidPhone : ResendConfirmationError
    data object TooManyRequestError : ResendConfirmationError
    data object ServerError : ResendConfirmationError
    data object NetworkError : ResendConfirmationError
    data class UnknownError(val message: String) : ResendConfirmationError
}

fun Throwable.toResendConfirmationError(): ResendConfirmationError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        400 -> ResendConfirmationError.InvalidPhone
        429 -> ResendConfirmationError.TooManyRequestError
        else -> ResendConfirmationError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> ResendConfirmationError.ServerError
    is NetworkErrorKind.Network -> ResendConfirmationError.NetworkError
    is NetworkErrorKind.Unknown -> ResendConfirmationError.UnknownError(kind.message)
}
