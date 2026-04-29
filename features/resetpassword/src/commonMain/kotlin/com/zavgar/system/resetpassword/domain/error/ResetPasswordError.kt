package com.zavgar.system.resetpassword.domain.error

import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError

sealed interface ResetPasswordError {
    data object InvalidPhoneError : ResetPasswordError
    data object UserNotFound : ResetPasswordError
    data object TooManyRequestError : ResetPasswordError
    data object ServerError : ResetPasswordError
    data object NetworkError : ResetPasswordError
    data class UnknownError(val message: String) : ResetPasswordError
}

fun Throwable.toResetPasswordError(): ResetPasswordError = when (val kind = classifyNetworkError()) {
    is NetworkErrorKind.Client -> when (kind.statusCode) {
        400 -> ResetPasswordError.InvalidPhoneError
        404 -> ResetPasswordError.UserNotFound
        429 -> ResetPasswordError.TooManyRequestError
        else -> ResetPasswordError.UnknownError(kind.message)
    }
    is NetworkErrorKind.Server -> ResetPasswordError.ServerError
    is NetworkErrorKind.Network -> ResetPasswordError.NetworkError
    is NetworkErrorKind.Unknown -> ResetPasswordError.UnknownError(kind.message)
}
