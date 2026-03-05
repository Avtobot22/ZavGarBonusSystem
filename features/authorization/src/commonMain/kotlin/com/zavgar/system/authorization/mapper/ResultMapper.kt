package com.zavgar.system.authorization.mapper

import com.zavgar.system.authorization.model.LoginResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.AuthError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_login
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_user_not_found

fun <T, E> AppResult<T, E>.toLoginResult(errorMapper: (E) -> SnackBarMessage): LoginResult {
    return when (this) {
        is AppResult.Success -> LoginResult.Success
        is AppResult.Error -> LoginResult.Error(errorMapper(this.error))
    }
}

fun AuthError.asSnackBarMessage() = when (this) {
    is AuthError.ValidationError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_invalid_login),
        type = SnackBarType.WARNING
    )

    is AuthError.UserNotFound -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
        type = SnackBarType.WARNING
    )

    is AuthError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is AuthError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is AuthError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is AuthError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message), // TODO поменять везде на простое сообщение что что-то пошло не так, пока для отладки оставил сообщение
        type = SnackBarType.ERROR
    )
}