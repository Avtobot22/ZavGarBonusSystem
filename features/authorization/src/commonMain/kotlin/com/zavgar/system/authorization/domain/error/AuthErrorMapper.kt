package com.zavgar.system.authorization.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

fun Throwable.toAuthError(): AuthError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        400 -> AuthError.ValidationError
        401 -> AuthError.UserNotFound
        429 -> AuthError.TooManyRequestError
        else -> AuthError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> AuthError.ServerError
    is NetworkErrorKind.Network -> AuthError.NetworkError
    is NetworkErrorKind.Unknown -> AuthError.UnknownError(kind.message)
}
