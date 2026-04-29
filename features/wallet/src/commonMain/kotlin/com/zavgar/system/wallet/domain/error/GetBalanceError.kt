package com.zavgar.system.wallet.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface GetBalanceError {
    data object TooManyRequestError : GetBalanceError
    data object ServerError : GetBalanceError
    data object NetworkError : GetBalanceError
    data class UnknownError(val message: String) : GetBalanceError
}

fun Throwable.toGetBalanceError(): GetBalanceError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        429 -> GetBalanceError.TooManyRequestError
        else -> GetBalanceError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> GetBalanceError.ServerError
    is NetworkErrorKind.Network -> GetBalanceError.NetworkError
    is NetworkErrorKind.Unknown -> GetBalanceError.UnknownError(kind.message)
}
