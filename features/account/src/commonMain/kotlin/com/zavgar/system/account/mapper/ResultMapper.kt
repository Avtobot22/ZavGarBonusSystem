package com.zavgar.system.account.mapper

import com.zavgar.system.account.model.ChangePasswordResult
import com.zavgar.system.account.model.DeleteResult
import com.zavgar.system.account.model.ProfileGetResult
import com.zavgar.system.account.model.ProfileUpdateResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.account.domain.error.ChangePasswordError
import com.zavgar.system.account.domain.error.DeleteError
import com.zavgar.system.account.domain.error.ProfileError
import com.zavgar.system.account.domain.model.ProfileResponse
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.resources.password_update_error

fun ProfileError.asSnackBarMessage() = when (this) {
    is ProfileError.ValidationError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_invalid_format),
        type = SnackBarType.WARNING
    )

    is ProfileError.UserNotFound -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
        type = SnackBarType.WARNING
    )

    is ProfileError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is ProfileError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is ProfileError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is ProfileError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}

fun ChangePasswordError.asSnackBarMessage() = when (this) {
    is ChangePasswordError.ValidationError -> SnackBarMessage(
        message = UiText.Resource(Res.string.password_update_error),
        type = SnackBarType.WARNING
    )

    is ChangePasswordError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is ChangePasswordError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is ChangePasswordError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is ChangePasswordError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}

fun DeleteError.asSnackBarMessage() = when (this) {
    is DeleteError.TooManyRequestError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_too_many_requests),
        type = SnackBarType.WARNING
    )

    is DeleteError.NetworkError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_network_error),
        type = SnackBarType.ERROR
    )

    is DeleteError.ServerError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_server_error),
        type = SnackBarType.ERROR
    )

    is DeleteError.UnknownError -> SnackBarMessage(
        message = UiText.DynamicString(this.message),
        type = SnackBarType.ERROR
    )
}

fun AppResult<Unit, ProfileError>.toProfileUpdateResult(): ProfileUpdateResult = when (this) {
    is AppResult.Success -> ProfileUpdateResult.Success
    is AppResult.Error -> ProfileUpdateResult.Error(error.asSnackBarMessage())
}

fun AppResult<Unit, ChangePasswordError>.toChangePasswordResult(): ChangePasswordResult = when (this) {
    is AppResult.Success -> ChangePasswordResult.Success
    is AppResult.Error -> ChangePasswordResult.Error(error.asSnackBarMessage())
}

fun AppResult<Unit, DeleteError>.toDeleteResult(): DeleteResult = when (this) {
    is AppResult.Success -> DeleteResult.Success
    is AppResult.Error -> DeleteResult.Error(error.asSnackBarMessage())
}

fun AppResult<ProfileResponse, ProfileError>.toProfileGetResult(): ProfileGetResult = when (this) {
    is AppResult.Success -> ProfileGetResult.Success(data)
    is AppResult.Error -> ProfileGetResult.Error(error.asSnackBarMessage())
}
