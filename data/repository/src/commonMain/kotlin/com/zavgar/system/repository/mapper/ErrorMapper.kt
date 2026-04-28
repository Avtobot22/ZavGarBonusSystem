package com.zavgar.system.repository.mapper

import com.zavgar.system.repository.model.error.AuthError
import com.zavgar.system.repository.model.error.ChangePasswordError
import com.zavgar.system.repository.model.error.ConfirmationError
import com.zavgar.system.repository.model.error.DeleteError
import com.zavgar.system.repository.model.error.GetBalanceError
import com.zavgar.system.repository.model.error.LogoutError
import com.zavgar.system.repository.model.error.OperationsError
import com.zavgar.system.repository.model.error.ProfileError
import com.zavgar.system.repository.model.error.RegisterError
import com.zavgar.system.repository.model.error.ResendConfirmationError
import com.zavgar.system.repository.model.error.ResetPasswordError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException

fun Throwable.toAuthError(): AuthError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                400 -> AuthError.ValidationError
                401 -> AuthError.UserNotFound
                429 -> AuthError.TooManyRequestError
                else -> AuthError.UnknownError(message)
            }
        }

        is ServerResponseException -> AuthError.ServerError

        is IOException -> AuthError.NetworkError

        else -> AuthError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toRegisterError(): RegisterError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                400 -> RegisterError.InvalidFormat
                409 -> RegisterError.UserAlreadyExists
                429 -> RegisterError.TooManyRequestError
                else -> RegisterError.UnknownError(message)
            }
        }

        is ServerResponseException -> RegisterError.ServerError

        is IOException -> RegisterError.NetworkError

        else -> RegisterError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toConfirmationError(): ConfirmationError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                400 -> ConfirmationError.InvalidCodeError
                429 -> ConfirmationError.TooManyRequestError
                else -> ConfirmationError.UnknownError(message)
            }
        }

        is ServerResponseException -> ConfirmationError.ServerError

        is IOException -> ConfirmationError.NetworkError

        else -> ConfirmationError.UnknownError(this.message ?: "Unknown error")
    }
}


fun Throwable.toResendConfirmationError(): ResendConfirmationError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                400 -> ResendConfirmationError.InvalidPhone
                429 -> ResendConfirmationError.TooManyRequestError
                else -> ResendConfirmationError.UnknownError(message)
            }
        }

        is ServerResponseException -> ResendConfirmationError.ServerError

        is IOException -> ResendConfirmationError.NetworkError

        else -> ResendConfirmationError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toResetPasswordError(): ResetPasswordError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                400 -> ResetPasswordError.InvalidPhoneError
                404 -> ResetPasswordError.UserNotFound
                429 -> ResetPasswordError.TooManyRequestError
                else -> ResetPasswordError.UnknownError(message)
            }
        }

        is ServerResponseException -> ResetPasswordError.ServerError

        is IOException -> ResetPasswordError.NetworkError

        else -> ResetPasswordError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toGetBalanceError(): GetBalanceError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                401 -> GetBalanceError.NotAuthorizedError
                429 -> GetBalanceError.TooManyRequestError
                else -> GetBalanceError.UnknownError(message)
            }
        }

        is ServerResponseException -> GetBalanceError.ServerError

        is IOException -> GetBalanceError.NetworkError

        else -> GetBalanceError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toLogoutError(): LogoutError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                401 -> LogoutError.NotAuthorizedError
                429 -> LogoutError.TooManyRequestError
                else -> LogoutError.UnknownError(message)
            }
        }

        is ServerResponseException -> LogoutError.ServerError

        is IOException -> LogoutError.NetworkError

        else -> LogoutError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toProfileError(): ProfileError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                400 -> ProfileError.ValidationError
                401 -> ProfileError.NotAuthorizedError
                404 -> ProfileError.UserNotFound
                429 -> ProfileError.TooManyRequestError
                else -> ProfileError.UnknownError(message)
            }
        }

        is ServerResponseException -> ProfileError.ServerError

        is IOException -> ProfileError.NetworkError

        else -> ProfileError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toChangePasswordError(): ChangePasswordError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                400 -> ChangePasswordError.ValidationError
                401 -> ChangePasswordError.NotAuthorizedError
                429 -> ChangePasswordError.TooManyRequestError
                else -> ChangePasswordError.UnknownError(message)
            }
        }

        is ServerResponseException -> ChangePasswordError.ServerError

        is IOException -> ChangePasswordError.NetworkError

        else -> ChangePasswordError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toDeleteError(): DeleteError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                401 -> DeleteError.NotAuthorizedError
                429 -> DeleteError.TooManyRequestError
                else -> DeleteError.UnknownError(message)
            }
        }

        is ServerResponseException -> DeleteError.ServerError

        is IOException -> DeleteError.NetworkError

        else -> DeleteError.UnknownError(this.message ?: "Unknown error")
    }
}

fun Throwable.toOperationsError(): OperationsError {
    return when (this) {
        is ClientRequestException -> {

            when (response.status.value) {
                400 -> OperationsError.ValidationError
                401 -> OperationsError.NotAuthorizedError
                404 -> OperationsError.UserNotFound
                429 -> OperationsError.TooManyRequestError
                else -> OperationsError.UnknownError(message)
            }
        }

        is ServerResponseException -> OperationsError.ServerError

        is IOException -> OperationsError.NetworkError

        else -> OperationsError.UnknownError(this.message ?: "Unknown error")
    }
}