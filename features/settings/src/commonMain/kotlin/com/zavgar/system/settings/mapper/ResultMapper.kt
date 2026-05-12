package com.zavgar.system.settings.mapper

import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.mapAppError
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.settings.model.LogoutResult

fun <T, E> AppResult<T, E>.toLogoutResult(errorMapper: (E) -> SnackBarMessage) = when (this) {
    is AppResult.Success -> LogoutResult.Success
    is AppResult.Error -> LogoutResult.Error(message = errorMapper(this.error))
}

fun LogoutError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is AppError -> mapAppError(this)
}
