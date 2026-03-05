package com.zavgar.system.account.mapper

import com.zavgar.system.account.model.ChangePasswordResult
import com.zavgar.system.account.model.DeleteResult
import com.zavgar.system.account.model.ProfileGetResult
import com.zavgar.system.account.model.ProfileUpdateResult
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.ChangePasswordError
import com.zavgar.system.domain.model.error.DeleteError
import com.zavgar.system.domain.model.error.ProfileError
import com.zavgar.system.domain.model.response.ProfileResponse
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_too_many_requests
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.resources.password_update_error
import com.zavgar.system.account.model.ProfileResponse as UiProfileResponse

fun <T, E> AppResult<T, E>.toProfileUpdateResult(
    errorMapper: (E) -> SnackBarMessage,
    isTokenExpired: (E) -> Boolean
): ProfileUpdateResult {
    return when (this) {
        is AppResult.Success -> ProfileUpdateResult.Success
        is AppResult.Error -> if (isTokenExpired(this.error)) {
            ProfileUpdateResult.TokenExpired
        } else {
            ProfileUpdateResult.Error(errorMapper(this.error))
        }
    }
}

fun <T, E> AppResult<T, E>.toChangePasswordResult(
    errorMapper: (E) -> SnackBarMessage,
    isTokenExpired: (E) -> Boolean
): ChangePasswordResult {
    return when (this) {
        is AppResult.Success -> ChangePasswordResult.Success
        is AppResult.Error -> if (isTokenExpired(this.error)) {
            ChangePasswordResult.TokenExpired
        } else {
            ChangePasswordResult.Error(errorMapper(this.error))
        }
    }
}

fun <T, E> AppResult<T, E>.toDeleteResult(
    errorMapper: (E) -> SnackBarMessage,
    isTokenExpired: (E) -> Boolean
): DeleteResult {
    return when (this) {
        is AppResult.Success -> DeleteResult.Success
        is AppResult.Error -> if (isTokenExpired(this.error)) {
            DeleteResult.TokenExpired
        } else {
            DeleteResult.Error(errorMapper(this.error))
        }
    }
}

fun <T, E> AppResult<T, E>.toProfileGetResult(
    errorMapper: (E) -> SnackBarMessage,
    dataMapper: (T) -> UiProfileResponse,
    isTokenExpired: (E) -> Boolean
): ProfileGetResult {
    return when (this) {
        is AppResult.Success -> ProfileGetResult.Success(dataMapper(this.data))
        is AppResult.Error -> if (isTokenExpired(this.error)) {
            ProfileGetResult.TokenExpired
        } else {
            ProfileGetResult.Error(errorMapper(this.error))
        }
    }
}

fun ProfileError.asSnackBarMessage() = when (this) {
    is ProfileError.ValidationError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_invalid_format),
        type = SnackBarType.WARNING
    )

    is ProfileError.NotAuthorizedError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
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

    is ChangePasswordError.NotAuthorizedError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
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
    is DeleteError.NotAuthorizedError -> SnackBarMessage(
        message = UiText.Resource(Res.string.error_user_not_found),
        type = SnackBarType.WARNING
    )

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

fun AppResult<Unit, ProfileError>.toProfileUpdateResult() =
    toProfileUpdateResult(ProfileError::asSnackBarMessage) { it is ProfileError.NotAuthorizedError }

fun AppResult<Unit, ChangePasswordError>.toChangePasswordResult() =
    toChangePasswordResult(ChangePasswordError::asSnackBarMessage) { it is ChangePasswordError.NotAuthorizedError }

fun AppResult<Unit, DeleteError>.toDeleteResult() =
    toDeleteResult(DeleteError::asSnackBarMessage) { it is DeleteError.NotAuthorizedError }

fun AppResult<ProfileResponse, ProfileError>.toProfileGetResult() =
    toProfileGetResult(
        ProfileError::asSnackBarMessage,
        ProfileResponse::toPresentation
    ) { it is ProfileError.NotAuthorizedError }