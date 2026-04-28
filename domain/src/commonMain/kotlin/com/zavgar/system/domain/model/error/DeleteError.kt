package com.zavgar.system.domain.model.error

sealed interface DeleteError {
    data object NotAuthorizedError : DeleteError, NotAuthorized
    data object TooManyRequestError : DeleteError

    data object ServerError : DeleteError

    data object NetworkError : DeleteError

    data class UnknownError(val message: String) : DeleteError
}