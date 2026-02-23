package com.zavgar.system.repository.model.error

sealed interface AuthError {
    data object UserNotFound : AuthError
    data object ServerError : AuthError
    data object NetworkError : AuthError
    data class UnknownError(val message: String) : AuthError
}