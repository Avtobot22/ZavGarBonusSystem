package com.zavgar.system.resetpassword.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.mapAppError
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.domain.auth.error.ResetPasswordError
import com.zavgar.system.resetpassword.model.ResetPasswordResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_phone
import com.zavgar.system.resources.error_user_not_found

fun <T, E> AppResult<T, E>.toResetPasswordResult(errorMapper: (E) -> SnackBarMessage): ResetPasswordResult {
    return when (this) {
        is AppResult.Success -> ResetPasswordResult.Success
        is AppResult.Error -> ResetPasswordResult.Error(errorMapper(this.error))
    }
}

fun ResetPasswordError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is ResetPasswordError.InvalidPhoneError -> SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_phone))
    is ResetPasswordError.UserNotFound -> SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found))
    is AppError -> mapAppError(this)
}
