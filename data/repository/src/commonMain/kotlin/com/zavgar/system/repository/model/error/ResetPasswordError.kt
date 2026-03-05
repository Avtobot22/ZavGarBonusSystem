package com.zavgar.system.repository.model.error

sealed interface ResetPasswordError {
    data object InvalidPhoneError : ResetPasswordError

    data object UserNotFound : ResetPasswordError

    data object TooManyRequestError : ResetPasswordError

    data object ServerError : ResetPasswordError

    data object NetworkError : ResetPasswordError

    data class UnknownError(val message: String) : ResetPasswordError
}