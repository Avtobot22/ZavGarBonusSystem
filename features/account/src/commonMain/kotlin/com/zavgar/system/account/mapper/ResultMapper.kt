package com.zavgar.system.account.mapper

import com.zavgar.system.account.model.DeleteResult
import com.zavgar.system.account.model.ProfileGetResult
import com.zavgar.system.account.model.ProfileUpdateResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.mapAppError
import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult

fun ProfileError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is ProfileError.ValidationError -> SnackBarMessage.warning(UiText.Resource(Res.string.error_invalid_format))
    is ProfileError.UserNotFound -> SnackBarMessage.warning(UiText.Resource(Res.string.error_user_not_found))
    is AppError -> mapAppError(this)
}

fun DeleteError.asSnackBarMessage(): SnackBarMessage = when (this) {
    is AppError -> mapAppError(this)
}

fun AppResult<UserProfile, ProfileError>.toProfileGetResult(): ProfileGetResult = when (this) {
    is AppResult.Success -> ProfileGetResult.Success(data)
    is AppResult.Error -> ProfileGetResult.Error(error.asSnackBarMessage())
}

fun AppResult<Unit, ProfileError>.toProfileUpdateResult(): ProfileUpdateResult = when (this) {
    is AppResult.Success -> ProfileUpdateResult.Success
    is AppResult.Error -> ProfileUpdateResult.Error(error.asSnackBarMessage())
}

fun AppResult<Unit, DeleteError>.toDeleteResult(): DeleteResult = when (this) {
    is AppResult.Success -> DeleteResult.Success
    is AppResult.Error -> DeleteResult.Error(error.asSnackBarMessage())
}
