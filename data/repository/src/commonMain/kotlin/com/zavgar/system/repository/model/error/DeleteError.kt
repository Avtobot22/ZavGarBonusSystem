package com.zavgar.system.repository.model.error

sealed interface DeleteError {
    data object NotAuthorizedError : DeleteError

    data object ServerError : DeleteError

    data object NetworkError : DeleteError

    data class UnknownError(val message: String) : DeleteError
}