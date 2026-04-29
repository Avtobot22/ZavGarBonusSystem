package com.zavgar.system.settings.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface LogoutError {
    data object TooManyRequestError : LogoutError
    data object ServerError : LogoutError
    data object NetworkError : LogoutError
    data class UnknownError(val message: String) : LogoutError
}

fun Throwable.toLogoutError(): LogoutError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        429 -> LogoutError.TooManyRequestError
        else -> LogoutError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> LogoutError.ServerError
    is NetworkErrorKind.Network -> LogoutError.NetworkError
    is NetworkErrorKind.Unknown -> LogoutError.UnknownError(kind.message)
}
