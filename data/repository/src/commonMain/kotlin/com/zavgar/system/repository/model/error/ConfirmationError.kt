package com.zavgar.system.repository.model.error

sealed interface ConfirmationError {

    data object InvalidCodeError : ConfirmationError

    data object ServerError : ConfirmationError

    data object NetworkError : ConfirmationError

    data class UnknownError(val message: String) : ConfirmationError
}