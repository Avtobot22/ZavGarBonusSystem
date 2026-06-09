package com.zavgar.system.confirmation.mapper

import com.zavgar.system.confirmation.model.ConfirmationResult
import com.zavgar.system.confirmation.model.ResendConfirmationResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.mapAppError
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_attempts_exceeded
import com.zavgar.system.resources.error_code_expired
import com.zavgar.system.resources.error_invalid_code
import com.zavgar.system.resources.error_invalid_phone
import com.zavgar.system.resources.error_session_expired
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult

fun <T, E> AppResult<T, E>.toConfirmationResult(errorMapper: (E) -> SnackBarMessage): ConfirmationResult {
    return when (this) {
        is AppResult.Success -> ConfirmationResult.Success
        is AppResult.Error -> ConfirmationResult.Error(errorMapper(this.error))
    }
}

fun ConfirmationError.asSnackBarMessage(): SnackBarMessage = when (this) {
    ConfirmationError.InvalidCodeError -> SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_code))
    ConfirmationError.CodeExpired -> SnackBarMessage.warning(UiText.Resource(Res.string.error_code_expired))
    ConfirmationError.SessionExpired -> SnackBarMessage.warning(UiText.Resource(Res.string.error_session_expired))
    ConfirmationError.AttemptsExceeded -> SnackBarMessage.warning(UiText.Resource(Res.string.error_attempts_exceeded))
    is AppError -> mapAppError(this)
}

fun <T, E> AppResult<T, E>.toResendConfirmationResult(errorMapper: (E) -> SnackBarMessage): ResendConfirmationResult {
    return when (this) {
        is AppResult.Success -> ResendConfirmationResult.Success
        is AppResult.Error -> ResendConfirmationResult.Error(errorMapper(this.error))
    }
}

fun ResendConfirmationError.asSnackBarMessage(): SnackBarMessage = when (this) {
    ResendConfirmationError.InvalidPhone -> SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_phone))
    ResendConfirmationError.SessionExpired -> SnackBarMessage.warning(UiText.Resource(Res.string.error_session_expired))
    is AppError -> mapAppError(this)
}
