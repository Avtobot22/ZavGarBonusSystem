package com.zavgar.system.domain.auth.error

import com.zavgar.system.utils.result.AppError

sealed interface ResendConfirmationError {
    data object InvalidPhone : ResendConfirmationError

    /** Сессия аутентификации истекла (410 AUTH_SESSION_EXPIRED) — начать процесс заново. */
    data object SessionExpired : ResendConfirmationError

    data class TooManyRequestError(
        override val retryAfterSeconds: Long? = null,
    ) : ResendConfirmationError, AppError.TooManyRequest

    data object ServerError : ResendConfirmationError, AppError.Server
    data object NetworkError : ResendConfirmationError, AppError.Network
    data class UnknownError(override val message: String) : ResendConfirmationError, AppError.Unknown
}
