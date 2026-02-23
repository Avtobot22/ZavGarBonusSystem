package com.zavgar.system.domain.model.error

sealed interface OperationsError {
    data object ValidationError : OperationsError

    data object NotAuthorizedError : OperationsError

    data object UserNotFound : OperationsError

    data object ServerError : OperationsError

    data object NetworkError : OperationsError

    data class UnknownError(val message: String) : OperationsError
}