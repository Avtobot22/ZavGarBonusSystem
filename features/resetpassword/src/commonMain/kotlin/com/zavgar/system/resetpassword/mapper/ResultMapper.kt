package com.zavgar.system.resetpassword.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.ResetPasswordError
import com.zavgar.system.resetpassword.model.ResetPasswordResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_phone
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_user_not_found

fun <T, E> AppResult<T, E>.toResetPasswordResult(errorMapper: (E) -> SnackBarMessage): ResetPasswordResult {
    return when (this) {
        is AppResult.Success -> ResetPasswordResult.Success
        is AppResult.Error -> ResetPasswordResult.Error(errorMapper(this.error))
    }
}

fun ResetPasswordError.asSnackBarMessage() = when (this) {
    is ResetPasswordError.InvalidPhoneError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_invalid_phone),
        type = SnackBarType.WARNING
    )

    is ResetPasswordError.UserNotFound -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
        type = SnackBarType.WARNING
    )

    is ResetPasswordError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is ResetPasswordError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is ResetPasswordError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is ResetPasswordError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}