package com.zavgar.system.domain.model.error


// TODO Нужно обрабатывать все ответы серва корректно
sealed interface ResendConfirmationError {
    data object InvalidPhone : ResendConfirmationError

    data object ServerError : ResendConfirmationError
    data object NetworkError : ResendConfirmationError
    data class UnknownError(val message: String) : ResendConfirmationError
}