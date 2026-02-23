package com.zavgar.system.registration.mapper

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.RegisterError
import com.zavgar.system.registration.model.RegisterResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_user_already_exists

fun <T, E> AppResult<T, E>.toRegisterResult(errorMapper: (E) -> UiText): RegisterResult {
    return when (this) {
        is AppResult.Success -> RegisterResult.Success
        is AppResult.Error -> RegisterResult.Error(errorMapper(this.error))
    }
}

fun RegisterError.asUiText() = when (this) {
    is RegisterError.UserAlreadyExists -> UiText.Resource(Res.string.error_user_already_exists)
    is RegisterError.InvalidFormat -> UiText.Resource(Res.string.error_invalid_format)
    is RegisterError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is RegisterError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is RegisterError.UnknownError -> UiText.DynamicString(this.message)
}