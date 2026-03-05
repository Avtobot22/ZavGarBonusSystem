package com.zavgar.system.repository.model.error

sealed interface GetBalanceError {

    data object NotAuthorizedError : GetBalanceError

    data object TooManyRequestError : GetBalanceError

    data object ServerError : GetBalanceError

    data object NetworkError : GetBalanceError

    data class UnknownError(val message: String) : GetBalanceError
}