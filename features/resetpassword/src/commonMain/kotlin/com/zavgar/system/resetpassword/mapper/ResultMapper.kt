package com.zavgar.system.resetpassword.mapper

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.ResetPasswordError
import com.zavgar.system.resetpassword.model.ResetPasswordResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_phone
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_user_not_found

fun <T, E> AppResult<T, E>.toResetPasswordResult(errorMapper: (E) -> UiText): ResetPasswordResult {
    return when (this) {
        is AppResult.Success -> ResetPasswordResult.Success
        is AppResult.Error -> ResetPasswordResult.Error(errorMapper(this.error))
    }
}

fun ResetPasswordError.asUiText() = when (this) {
    is ResetPasswordError.InvalidPhoneError -> UiText.Resource(Res.string.error_invalid_phone)
    is ResetPasswordError.UserNotFound -> UiText.Resource(Res.string.error_user_not_found)
    is ResetPasswordError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is ResetPasswordError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is ResetPasswordError.UnknownError -> UiText.DynamicString(this.message)
}