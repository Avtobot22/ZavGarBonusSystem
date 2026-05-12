package com.zavgar.system.domain.userinfo.error

import com.zavgar.system.utils.result.AppError

sealed interface ProfileError {
    data object ValidationError : ProfileError
    data object UserNotFound : ProfileError
    data object TooManyRequestError : ProfileError, AppError.TooManyRequest
    data object ServerError : ProfileError, AppError.Server
    data object NetworkError : ProfileError, AppError.Network
    data class UnknownError(override val message: String) : ProfileError, AppError.Unknown
}
