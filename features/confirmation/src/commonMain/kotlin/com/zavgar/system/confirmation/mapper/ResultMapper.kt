package com.zavgar.system.confirmation.mapper

import com.zavgar.system.confirmation.model.ConfirmationResult
import com.zavgar.system.confirmation.model.ResendConfirmationResult
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.ConfirmationError
import com.zavgar.system.domain.model.error.ResendConfirmationError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_code
import com.zavgar.system.resources.error_invalid_phone
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error

fun <T, E> AppResult<T, E>.toConfirmationResult(errorMapper: (E) -> UiText): ConfirmationResult {
    return when (this) {
        is AppResult.Success -> ConfirmationResult.Success
        is AppResult.Error -> ConfirmationResult.Error(errorMapper(this.error))
    }
}

fun ConfirmationError.asUiText() = when (this) {
    ConfirmationError.InvalidCodeError -> UiText.Resource(Res.string.error_invalid_code)
    ConfirmationError.ServerError -> UiText.Resource(Res.string.error_server_error)
    ConfirmationError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is ConfirmationError.UnknownError -> UiText.DynamicString(this.message)
}

fun <T, E> AppResult<T, E>.toResendConfirmationResult(errorMapper: (E) -> UiText): ResendConfirmationResult {
    return when (this) {
        is AppResult.Success -> ResendConfirmationResult.Success
        is AppResult.Error -> ResendConfirmationResult.Error(errorMapper(this.error))
    }
}

fun ResendConfirmationError.asUiText() = when (this) {
    ResendConfirmationError.InvalidPhone -> UiText.Resource(Res.string.error_invalid_phone)
    ResendConfirmationError.ServerError -> UiText.Resource(Res.string.error_server_error)
    ResendConfirmationError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is ResendConfirmationError.UnknownError -> UiText.DynamicString(this.message)
}