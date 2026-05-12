package com.zavgar.system.registration.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.mapAppError
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.registration.model.RegisterResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_user_already_exists

fun <T, E> AppResult<T, E>.toRegisterResult(errorMapper: (E) -> SnackBarMessage): RegisterResult {
    return when (this) {
        is AppResult.Success -> RegisterResult.Success
        is AppResult.Error -> RegisterResult.Error(errorMapper(this.error))
    }
}

fun RegisterError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is RegisterError.UserAlreadyExists -> SnackBarMessage.warning(UiText.Resource(Res.string.error_user_already_exists))
    is RegisterError.InvalidFormat -> SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_format))
    is AppError -> mapAppError(this)
}
