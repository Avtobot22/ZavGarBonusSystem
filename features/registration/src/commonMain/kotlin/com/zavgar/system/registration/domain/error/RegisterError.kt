package com.zavgar.system.registration.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface RegisterError {
    data object InvalidFormat : RegisterError
    data object UserAlreadyExists : RegisterError
    data object TooManyRequestError : RegisterError
    data object NetworkError : RegisterError
    data object ServerError : RegisterError
    data class UnknownError(val message: String) : RegisterError
}

fun Throwable.toRegisterError(): RegisterError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        400 -> RegisterError.InvalidFormat
        409 -> RegisterError.UserAlreadyExists
        429 -> RegisterError.TooManyRequestError
        else -> RegisterError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> RegisterError.ServerError
    is NetworkErrorKind.Network -> RegisterError.NetworkError
    is NetworkErrorKind.Unknown -> RegisterError.UnknownError(kind.message)
}
