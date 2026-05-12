package com.zavgar.system.domain.session.error

import com.zavgar.system.utils.result.AppError

sealed interface SessionError {
    data object NotFound : SessionError
    data class UnknownError(override val message: String) : SessionError, AppError.Unknown
}
