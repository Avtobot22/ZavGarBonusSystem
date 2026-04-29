package com.zavgar.system.account.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface ChangePasswordError {
    data object ValidationError : ChangePasswordError
    data object TooManyRequestError : ChangePasswordError
    data object ServerError : ChangePasswordError
    data object NetworkError : ChangePasswordError
    data class UnknownError(val message: String) : ChangePasswordError
}

fun Throwable.toChangePasswordError(): ChangePasswordError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        400 -> ChangePasswordError.ValidationError
        429 -> ChangePasswordError.TooManyRequestError
        else -> ChangePasswordError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> ChangePasswordError.ServerError
    is NetworkErrorKind.Network -> ChangePasswordError.NetworkError
    is NetworkErrorKind.Unknown -> ChangePasswordError.UnknownError(kind.message)
}
