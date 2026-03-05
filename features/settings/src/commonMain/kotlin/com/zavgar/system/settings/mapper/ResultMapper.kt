package com.zavgar.system.settings.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.LogoutError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.settings.model.LogoutResult


fun <T, E> AppResult<T, E>.toLogoutResult(errorMapper: (E) -> SnackBarMessage) = when (this) {
    is AppResult.Success -> LogoutResult.Success
    is AppResult.Error -> LogoutResult.Error(message = errorMapper(this.error))
}

fun LogoutError.asSnackBarMessage() = when (this) {
    is LogoutError.NotAuthorizedError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
        type = SnackBarType.WARNING
    )

    is LogoutError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is LogoutError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is LogoutError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is LogoutError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}