package com.zavgar.system.domain.model.error

// TODO Нужно обрабатывать все ответы серва корректно
sealed interface RegisterError {

    data object InvalidFormat : RegisterError

    data object UserAlreadyExists : RegisterError

    data object TooManyRequestError : RegisterError

    data object NetworkError : RegisterError

    data object ServerError : RegisterError

    data class UnknownError(val message: String) : RegisterError
}