package com.zavgar.system.account.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface ProfileError {
    data object ValidationError : ProfileError
    data object UserNotFound : ProfileError
    data object TooManyRequestError : ProfileError
    data object ServerError : ProfileError
    data object NetworkError : ProfileError
    data class UnknownError(val message: String) : ProfileError
}

fun Throwable.toProfileError(): ProfileError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        400 -> ProfileError.ValidationError
        404 -> ProfileError.UserNotFound
        429 -> ProfileError.TooManyRequestError
        else -> ProfileError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> ProfileError.ServerError
    is NetworkErrorKind.Network -> ProfileError.NetworkError
    is NetworkErrorKind.Unknown -> ProfileError.UnknownError(kind.message)
}
