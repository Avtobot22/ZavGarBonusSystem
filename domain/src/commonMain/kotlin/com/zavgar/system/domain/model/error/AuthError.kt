package com.zavgar.system.domain.model.error

// TODO Нужно обрабатывать все ответы серва корректно
sealed interface AuthError {
    data object UserNotFound : AuthError

    data object ServerError : AuthError

    data object NetworkError : AuthError

    data class UnknownError(val message: String) : AuthError
}