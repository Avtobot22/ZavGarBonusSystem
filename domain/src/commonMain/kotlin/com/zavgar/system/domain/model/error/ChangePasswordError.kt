package com.zavgar.system.domain.model.error

sealed interface ChangePasswordError {

    data object ValidationError : ChangePasswordError

    data object NotAuthorizedError : ChangePasswordError, NotAuthorized

    data object TooManyRequestError : ChangePasswordError

    data object ServerError : ChangePasswordError

    data object NetworkError : ChangePasswordError

    data class UnknownError(val message: String) : ChangePasswordError
}