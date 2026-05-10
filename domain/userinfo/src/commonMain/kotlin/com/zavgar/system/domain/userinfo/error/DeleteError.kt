package com.zavgar.system.domain.userinfo.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface DeleteError {
    data object TooManyRequestError : DeleteError
    data object ServerError : DeleteError
    data object NetworkError : DeleteError
    data class UnknownError(val message: String) : DeleteError
}

fun Throwable.toDeleteError(): DeleteError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        429 -> DeleteError.TooManyRequestError
        else -> DeleteError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> DeleteError.ServerError
    is NetworkErrorKind.Network -> DeleteError.NetworkError
    is NetworkErrorKind.Unknown -> DeleteError.UnknownError(kind.message)
}
