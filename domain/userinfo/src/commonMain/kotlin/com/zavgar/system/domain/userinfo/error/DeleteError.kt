package com.zavgar.system.domain.userinfo.error

import com.zavgar.system.utils.result.AppError

sealed interface DeleteError {
    data object TooManyRequestError : DeleteError, AppError.TooManyRequest
    data object ServerError : DeleteError, AppError.Server
    data object NetworkError : DeleteError, AppError.Network
    data class UnknownError(override val message: String) : DeleteError, AppError.Unknown
}
