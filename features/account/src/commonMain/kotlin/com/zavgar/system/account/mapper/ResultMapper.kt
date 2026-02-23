package com.zavgar.system.account.mapper

import com.zavgar.system.account.model.ChangePasswordResult
import com.zavgar.system.account.model.DeleteResult
import com.zavgar.system.account.model.ProfileGetResult
import com.zavgar.system.account.model.ProfileUpdateResult
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.response.ProfileResponse
import com.zavgar.system.domain.model.error.ChangePasswordError
import com.zavgar.system.domain.model.error.DeleteError
import com.zavgar.system.domain.model.error.ProfileError
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_invalid_format
import com.zavgar.system.resources.error_network_error
import com.zavgar.system.resources.error_server_error
import com.zavgar.system.resources.error_user_not_found
import com.zavgar.system.account.model.ProfileResponse as UiProfileResponse

fun <T, E> AppResult<T, E>.toProfileUpdateResult(
    errorMapper: (E) -> UiText,
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
    errorMapper: (E) -> UiText,
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
    errorMapper: (E) -> UiText,
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
    errorMapper: (E) -> UiText,
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

fun ProfileError.asUiText() = when (this) {
    is ProfileError.ValidationError -> UiText.Resource(Res.string.error_invalid_format)
    is ProfileError.NotAuthorizedError -> UiText.Resource(Res.string.error_user_not_found)
    is ProfileError.UserNotFound -> UiText.Resource(Res.string.error_user_not_found)
    is ProfileError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is ProfileError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is ProfileError.UnknownError -> UiText.DynamicString(this.message)
}

fun ChangePasswordError.asUiText() = when (this) {
    is ChangePasswordError.ValidationError -> UiText.Resource(Res.string.error_invalid_format)
    is ChangePasswordError.NotAuthorizedError -> UiText.Resource(Res.string.error_user_not_found)
    is ChangePasswordError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is ChangePasswordError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is ChangePasswordError.UnknownError -> UiText.DynamicString(this.message)
}

fun DeleteError.asUiText() = when (this) {
    is DeleteError.NotAuthorizedError -> UiText.Resource(Res.string.error_user_not_found)
    is DeleteError.NetworkError -> UiText.Resource(Res.string.error_network_error)
    is DeleteError.ServerError -> UiText.Resource(Res.string.error_server_error)
    is DeleteError.UnknownError -> UiText.DynamicString(this.message)
}

fun AppResult<Unit, ProfileError>.toProfileUpdateResult() =
    toProfileUpdateResult(ProfileError::asUiText) { it is ProfileError.NotAuthorizedError }

fun AppResult<Unit, ChangePasswordError>.toChangePasswordResult() =
    toChangePasswordResult(ChangePasswordError::asUiText) { it is ChangePasswordError.NotAuthorizedError }

fun AppResult<Unit, DeleteError>.toDeleteResult() =
    toDeleteResult(DeleteError::asUiText) { it is DeleteError.NotAuthorizedError }

fun AppResult<ProfileResponse, ProfileError>.toProfileGetResult() =
    toProfileGetResult(
        ProfileError::asUiText,
        ProfileResponse::toPresentation
    ) { it is ProfileError.NotAuthorizedError }