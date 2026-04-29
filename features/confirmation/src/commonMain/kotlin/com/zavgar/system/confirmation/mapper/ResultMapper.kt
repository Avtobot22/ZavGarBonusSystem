package com.zavgar.system.confirmation.mapper

import com.zavgar.system.confirmation.model.ConfirmationResult
import com.zavgar.system.confirmation.model.ResendConfirmationResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.confirmation.domain.error.ConfirmationError
import com.zavgar.system.confirmation.domain.error.ResendConfirmationError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_code
import com.zavgar.system.resources.error_invalid_phone
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests

fun <T, E> AppResult<T, E>.toConfirmationResult(errorMapper: (E) -> SnackBarMessage): ConfirmationResult {
    return when (this) {
        is AppResult.Success -> ConfirmationResult.Success
        is AppResult.Error -> ConfirmationResult.Error(errorMapper(this.error))
    }
}

fun ConfirmationError.asSnackBarMessage() = when (this) {
    ConfirmationError.InvalidCodeError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_invalid_code),
        type = SnackBarType.WARNING
    )

    ConfirmationError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    ConfirmationError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    ConfirmationError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is ConfirmationError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}

fun <T, E> AppResult<T, E>.toResendConfirmationResult(errorMapper: (E) -> SnackBarMessage): ResendConfirmationResult {
    return when (this) {
        is AppResult.Success -> ResendConfirmationResult.Success
        is AppResult.Error -> ResendConfirmationResult.Error(errorMapper(this.error))
    }
}

fun ResendConfirmationError.asSnackBarMessage() = when (this) {
    ResendConfirmationError.InvalidPhone -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_invalid_phone),
        type = SnackBarType.WARNING
    )

    ResendConfirmationError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    ResendConfirmationError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    ResendConfirmationError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is ResendConfirmationError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}