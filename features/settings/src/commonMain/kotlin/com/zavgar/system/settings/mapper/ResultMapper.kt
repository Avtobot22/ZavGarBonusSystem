package com.zavgar.system.settings.mapper

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.LogoutError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.settings.model.LogoutResult


fun <T, E> AppResult<T, E>.toLogoutResult(errorMapper: (E) -> UiText) = when (this) {
    is AppResult.Success -> LogoutResult.Success
    is AppResult.Error -> LogoutResult.Error(message = errorMapper(this.error))
}

fun LogoutError.asUiText() = when (this) {
    is LogoutError.NotAuthorizedError -> UiText.Resource(Res.string.error_user_not_found)
    is LogoutError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is LogoutError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is LogoutError.UnknownError -> UiText.DynamicString(this.message)
}