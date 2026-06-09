package com.zavgar.system.domain.auth.error

import com.zavgar.system.utils.result.AppError

sealed interface ConfirmationError {
    data object InvalidCodeError : ConfirmationError

    /** OTP-код истёк (410 CONFIRMATION_CODE_EXPIRED) — нужно запросить новый. */
    data object CodeExpired : ConfirmationError

    /** Сессия аутентификации истекла (410 AUTH_SESSION_EXPIRED) — начать процесс заново. */
    data object SessionExpired : ConfirmationError

    /** Превышено число попыток ввода кода (429 CONFIRMATION_ATTEMPTS_EXCEEDED) — код аннулирован. */
    data object AttemptsExceeded : ConfirmationError

    data class TooManyRequestError(
        override val retryAfterSeconds: Long? = null,
    ) : ConfirmationError, AppError.TooManyRequest

    data object ServerError : ConfirmationError, AppError.Server
    data object NetworkError : ConfirmationError, AppError.Network
    data class UnknownError(override val message: String) : ConfirmationError, AppError.Unknown
}
