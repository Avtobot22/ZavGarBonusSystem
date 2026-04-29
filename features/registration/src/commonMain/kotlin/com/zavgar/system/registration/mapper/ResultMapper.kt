package com.zavgar.system.registration.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.registration.domain.error.RegisterError
import com.zavgar.system.registration.model.RegisterResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_user_already_exists

fun <T, E> AppResult<T, E>.toRegisterResult(errorMapper: (E) -> SnackBarMessage): RegisterResult {
    return when (this) {
        is AppResult.Success -> RegisterResult.Success
        is AppResult.Error -> RegisterResult.Error(errorMapper(this.error))
    }
}

fun RegisterError.asSnackBarMessage() = when (this) {
    is RegisterError.UserAlreadyExists -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_already_exists),
        type = SnackBarType.WARNING
    )

    is RegisterError.InvalidFormat -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_invalid_format),
        type = SnackBarType.WARNING
    )

    is RegisterError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is RegisterError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is RegisterError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is RegisterError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}