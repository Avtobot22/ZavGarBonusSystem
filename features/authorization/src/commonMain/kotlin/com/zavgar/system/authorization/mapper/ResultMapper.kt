package com.zavgar.system.authorization.mapper

import com.zavgar.system.authorization.model.LoginResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.mapAppError
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_login
import com.zavgar.system.resources.error_user_not_found

fun <T, E> AppResult<T, E>.toLoginResult(errorMapper: (E) -> SnackBarMessage): LoginResult {
    return when (this) {
        is AppResult.Success -> LoginResult.Success
        is AppResult.Error -> LoginResult.Error(errorMapper(this.error))
    }
}

fun AuthError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is AuthError.ValidationError -> SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_login))
    is AuthError.UserNotFound -> SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found))
    is AppError -> mapAppError(this)
}
