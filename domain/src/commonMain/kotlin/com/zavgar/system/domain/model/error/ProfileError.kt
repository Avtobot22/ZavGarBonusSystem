package com.zavgar.system.domain.model.error

sealed interface ProfileError {
    data object ValidationError : ProfileError

    data object NotAuthorizedError : ProfileError

    data object UserNotFound : ProfileError

    data object TooManyRequestError : ProfileError

    data object ServerError : ProfileError

    data object NetworkError : ProfileError

    data class UnknownError(val message: String) : ProfileError
}