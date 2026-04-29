package com.zavgar.system.authorization.domain.error

sealed interface AuthError {
    data object ValidationError : AuthError
    data object UserNotFound : AuthError
    data object TooManyRequestError : AuthError
    data object ServerError : AuthError
    data object NetworkError : AuthError
    data class UnknownError(val message: String) : AuthError
}
