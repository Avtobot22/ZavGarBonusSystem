package com.zavgar.system.authorization.mapper

import com.zavgar.system.authorization.model.LoginResult
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.AuthError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_user_not_found

fun <T, E> AppResult<T, E>.toLoginResult(errorMapper: (E) -> UiText): LoginResult {
    return when (this) {
        is AppResult.Success -> LoginResult.Success
        is AppResult.Error -> LoginResult.Error(errorMapper(this.error))
    }
}

fun AuthError.asUiText() = when (this) {
    is AuthError.UserNotFound -> UiText.Resource(Res.string.error_user_not_found)
    is AuthError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is AuthError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is AuthError.UnknownError -> UiText.DynamicString(this.message)
}