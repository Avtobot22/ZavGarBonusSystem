package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.response.Balance
import com.zavgar.system.repository.model.response.BalanceResponse
import com.zavgar.system.repository.model.response.ProfileResponse
import com.zavgar.system.domain.model.error.AuthError as DomainAuthError
import com.zavgar.system.domain.model.error.ChangePasswordError as DomainChangePasswordError
import com.zavgar.system.domain.model.error.ConfirmationError as DomainConfirmationError
import com.zavgar.system.domain.model.error.DeleteError as DomainDeleteError
import com.zavgar.system.domain.model.error.GetBalanceError as DomainGetBalanceError
import com.zavgar.system.domain.model.error.LogoutError as DomainLogoutError
import com.zavgar.system.domain.model.error.OperationsError as DomainOperationsError
import com.zavgar.system.domain.model.error.ProfileError as DomainProfileError
import com.zavgar.system.domain.model.error.RegisterError as DomainRegisterError
import com.zavgar.system.domain.model.error.ResendConfirmationError as DomainResendConfirmationError
import com.zavgar.system.domain.model.error.ResetPasswordError as DomainResetPasswordError
import com.zavgar.system.domain.model.response.ProfileResponse as DomainProfileResponse
import com.zavgar.system.domain.model.response.TransactionsPageResponse as DomainTransactionsPageResponse
import com.zavgar.system.repository.model.error.AuthError as RepoAuthError
import com.zavgar.system.repository.model.error.ChangePasswordError as RepoChangePasswordError
import com.zavgar.system.repository.model.error.ConfirmationError as RepoConfirmationError
import com.zavgar.system.repository.model.error.DeleteError as RepoDeleteError
import com.zavgar.system.repository.model.error.GetBalanceError as RepoGetBalanceError
import com.zavgar.system.repository.model.error.LogoutError as RepoLogoutError
import com.zavgar.system.repository.model.error.OperationsError as RepoOperationsError
import com.zavgar.system.repository.model.error.ProfileError as RepoProfileError
import com.zavgar.system.repository.model.error.RegisterError as RepoRegisterError
import com.zavgar.system.repository.model.error.ResendConfirmationError as RepoResendConfirmationError
import com.zavgar.system.repository.model.error.ResetPasswordError as RepoResetPasswordError
import com.zavgar.system.repository.model.response.TransactionsPageResponse as RepoTransactionsPageResponse

fun RepoAuthError.toDomain() = when (this) {
    RepoAuthError.ValidationError -> DomainAuthError.ValidationError
    RepoAuthError.UserNotFound -> DomainAuthError.UserNotFound
    RepoAuthError.TooManyRequestError -> DomainAuthError.TooManyRequestError
    RepoAuthError.ServerError -> DomainAuthError.ServerError
    RepoAuthError.NetworkError -> DomainAuthError.NetworkError
    is RepoAuthError.UnknownError -> DomainAuthError.UnknownError(this.message)
}

fun RepoRegisterError.toDomain() = when (this) {
    RepoRegisterError.InvalidFormat -> DomainRegisterError.InvalidFormat
    RepoRegisterError.UserAlreadyExists -> DomainRegisterError.UserAlreadyExists
    RepoRegisterError.TooManyRequestError -> DomainRegisterError.TooManyRequestError
    RepoRegisterError.NetworkError -> DomainRegisterError.NetworkError
    RepoRegisterError.ServerError -> DomainRegisterError.ServerError
    is RepoRegisterError.UnknownError -> DomainRegisterError.UnknownError(this.message)
}

fun RepoConfirmationError.toDomain() = when (this) {
    RepoConfirmationError.InvalidCodeError -> DomainConfirmationError.InvalidCodeError
    RepoConfirmationError.TooManyRequestError -> DomainConfirmationError.TooManyRequestError
    RepoConfirmationError.NetworkError -> DomainConfirmationError.NetworkError
    RepoConfirmationError.ServerError -> DomainConfirmationError.ServerError
    is RepoConfirmationError.UnknownError -> DomainConfirmationError.UnknownError(this.message)
}

fun RepoResendConfirmationError.toDomain() = when (this) {
    RepoResendConfirmationError.InvalidPhone -> DomainResendConfirmationError.InvalidPhone
    RepoResendConfirmationError.TooManyRequestError -> DomainResendConfirmationError.TooManyRequestError
    RepoResendConfirmationError.NetworkError -> DomainResendConfirmationError.NetworkError
    RepoResendConfirmationError.ServerError -> DomainResendConfirmationError.ServerError
    is RepoResendConfirmationError.UnknownError -> DomainResendConfirmationError.UnknownError(this.message)
}

fun RepoResetPasswordError.toDomain() = when (this) {
    RepoResetPasswordError.InvalidPhoneError -> DomainResetPasswordError.InvalidPhoneError
    RepoResetPasswordError.UserNotFound -> DomainResetPasswordError.UserNotFound
    RepoResetPasswordError.TooManyRequestError -> DomainResetPasswordError.TooManyRequestError
    RepoResetPasswordError.NetworkError -> DomainResetPasswordError.NetworkError
    RepoResetPasswordError.ServerError -> DomainResetPasswordError.ServerError
    is RepoResetPasswordError.UnknownError -> DomainResetPasswordError.UnknownError(this.message)
}

fun RepoGetBalanceError.toDomain() = when (this) {
    RepoGetBalanceError.NotAuthorizedError -> DomainGetBalanceError.NotAuthorizedError
    RepoGetBalanceError.TooManyRequestError -> DomainGetBalanceError.TooManyRequestError
    RepoGetBalanceError.NetworkError -> DomainGetBalanceError.NetworkError
    RepoGetBalanceError.ServerError -> DomainGetBalanceError.ServerError
    is RepoGetBalanceError.UnknownError -> DomainGetBalanceError.UnknownError(this.message)
}

fun RepoLogoutError.toDomain() = when (this) {
    RepoLogoutError.NotAuthorizedError -> DomainLogoutError.NotAuthorizedError
    RepoLogoutError.TooManyRequestError -> DomainLogoutError.TooManyRequestError
    RepoLogoutError.NetworkError -> DomainLogoutError.NetworkError
    RepoLogoutError.ServerError -> DomainLogoutError.ServerError
    is RepoLogoutError.UnknownError -> DomainLogoutError.UnknownError(this.message)
}

fun RepoProfileError.toDomain() = when (this) {
    RepoProfileError.ValidationError -> DomainProfileError.ValidationError
    RepoProfileError.NotAuthorizedError -> DomainProfileError.NotAuthorizedError
    RepoProfileError.UserNotFound -> DomainProfileError.UserNotFound
    RepoProfileError.TooManyRequestError -> DomainProfileError.TooManyRequestError
    RepoProfileError.NetworkError -> DomainProfileError.NetworkError
    RepoProfileError.ServerError -> DomainProfileError.ServerError
    is RepoProfileError.UnknownError -> DomainProfileError.UnknownError(this.message)
}

fun RepoChangePasswordError.toDomain() = when (this) {
    RepoChangePasswordError.ValidationError -> DomainChangePasswordError.ValidationError
    RepoChangePasswordError.NotAuthorizedError -> DomainChangePasswordError.NotAuthorizedError
    RepoChangePasswordError.TooManyRequestError -> DomainChangePasswordError.TooManyRequestError
    RepoChangePasswordError.NetworkError -> DomainChangePasswordError.NetworkError
    RepoChangePasswordError.ServerError -> DomainChangePasswordError.ServerError
    is RepoChangePasswordError.UnknownError -> DomainChangePasswordError.UnknownError(this.message)
}

fun RepoDeleteError.toDomain() = when (this) {
    RepoDeleteError.NotAuthorizedError -> DomainDeleteError.NotAuthorizedError
    RepoDeleteError.TooManyRequestError -> DomainDeleteError.TooManyRequestError
    RepoDeleteError.NetworkError -> DomainDeleteError.NetworkError
    RepoDeleteError.ServerError -> DomainDeleteError.ServerError
    is RepoDeleteError.UnknownError -> DomainDeleteError.UnknownError(this.message)
}

fun RepoOperationsError.toDomain() = when (this) {
    RepoOperationsError.ValidationError -> DomainOperationsError.ValidationError
    RepoOperationsError.NotAuthorizedError -> DomainOperationsError.NotAuthorizedError
    RepoOperationsError.UserNotFound -> DomainOperationsError.UserNotFound
    RepoOperationsError.TooManyRequestError -> DomainOperationsError.TooManyRequestError
    RepoOperationsError.NetworkError -> DomainOperationsError.NetworkError
    RepoOperationsError.ServerError -> DomainOperationsError.ServerError
    is RepoOperationsError.UnknownError -> DomainOperationsError.UnknownError(this.message)
}

inline fun <T, E_Repo, E_Domain> AppResult<T, E_Repo>.mapError(
    crossinline errorMapper: (E_Repo) -> E_Domain,
): AppResult<T, E_Domain> = when (this) {
    is AppResult.Success -> AppResult.Success(this.data)
    is AppResult.Error -> AppResult.Error(errorMapper(this.error))
}

fun AppResult<Unit, RepoAuthError>.toDomainAuth(): AppResult<Unit, DomainAuthError> =
    mapError(RepoAuthError::toDomain)

fun AppResult<Unit, RepoRegisterError>.toDomainRegister(): AppResult<Unit, DomainRegisterError> =
    mapError(RepoRegisterError::toDomain)

fun AppResult<Unit, RepoConfirmationError>.toDomainConfirmation(): AppResult<Unit, DomainConfirmationError> =
    mapError(RepoConfirmationError::toDomain)

fun AppResult<Unit, RepoResendConfirmationError>.toDomainResendConfirmation(): AppResult<Unit, DomainResendConfirmationError> =
    mapError(RepoResendConfirmationError::toDomain)

fun AppResult<Unit, RepoResetPasswordError>.toDomainResetPassword(): AppResult<Unit, DomainResetPasswordError> =
    mapError(RepoResetPasswordError::toDomain)

fun AppResult<BalanceResponse, RepoGetBalanceError>.toDomainBalance(): AppResult<Balance, DomainGetBalanceError> =
    mapError(RepoGetBalanceError::toDomain).let {
        when (it) {
            is AppResult.Success -> AppResult.Success(it.data.toDomain())
            is AppResult.Error -> AppResult.Error(it.error)
        }
    }

fun AppResult<Unit, RepoLogoutError>.toDomainLogout(): AppResult<Unit, DomainLogoutError> =
    mapError(RepoLogoutError::toDomain)

fun AppResult<Unit, RepoProfileError>.toDomainProfileUpdate(): AppResult<Unit, DomainProfileError> =
    mapError(RepoProfileError::toDomain)

fun AppResult<ProfileResponse, RepoProfileError>.toDomainProfile(): AppResult<DomainProfileResponse, DomainProfileError> =
    mapError(RepoProfileError::toDomain).let {
        when (it) {
            is AppResult.Success -> AppResult.Success(it.data.toDomain())
            is AppResult.Error -> AppResult.Error(it.error)
        }
    }

fun AppResult<Unit, RepoChangePasswordError>.toDomainChangePassword(): AppResult<Unit, DomainChangePasswordError> =
    mapError(RepoChangePasswordError::toDomain)

fun AppResult<Unit, RepoDeleteError>.toDomainDelete(): AppResult<Unit, DomainDeleteError> =
    mapError(RepoDeleteError::toDomain)

fun AppResult<RepoTransactionsPageResponse, RepoOperationsError>.toDomainOperations(): AppResult<DomainTransactionsPageResponse, DomainOperationsError> =
    mapError(RepoOperationsError::toDomain).let {
        when (it) {
            is AppResult.Success -> AppResult.Success(it.data.toDomain())
            is AppResult.Error -> AppResult.Error(it.error)
        }
    }
