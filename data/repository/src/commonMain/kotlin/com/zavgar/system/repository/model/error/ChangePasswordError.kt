package com.zavgar.system.repository.model.error

sealed interface ChangePasswordError {

    data object ValidationError : ChangePasswordError

    data object NotAuthorizedError : ChangePasswordError

    data object ServerError : ChangePasswordError

    data object NetworkError : ChangePasswordError

    data class UnknownError(val message: String) : ChangePasswordError
}