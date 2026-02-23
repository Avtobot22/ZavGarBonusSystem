package com.zavgar.system.domain.model.error

sealed interface LogoutError {
    data object NotAuthorizedError : LogoutError

    data object ServerError : LogoutError

    data object NetworkError : LogoutError

    data class UnknownError(val message: String) : LogoutError
}