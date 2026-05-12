package com.zavgar.system.core.presentation.util

import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.utils.result.AppError

fun AppError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is AppError.TooManyRequest -> SnackBarMessage.warning(UiText.Resource(Res.string.error_too_many_requests))
    is AppError.Server -> SnackBarMessage.error(UiText.Resource(Res.string.error_server_error))
    is AppError.Network -> SnackBarMessage.error(UiText.Resource(Res.string.error_network_error))
    is AppError.Unknown -> SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
}

fun mapAppError(error: AppError): SnackBarMessage = error.asSnackBarMessage()
