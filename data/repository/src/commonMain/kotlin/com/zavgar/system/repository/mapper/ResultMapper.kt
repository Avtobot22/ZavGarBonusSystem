package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.response.Balance
import com.zavgar.system.repository.model.response.BalanceResponse
import com.zavgar.system.repository.model.response.ProfileResponse
import com.zavgar.system.domain.model.AppResult as DomainAppResult
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
import com.zavgar.system.repository.model.AppResult as RepoAppResult
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

/**
 * Маппер для AuthError.
 */
fun RepoAuthError.toDomain() = when (this) {
    RepoAuthError.UserNotFound -> DomainAuthError.UserNotFound
    RepoAuthError.ServerError -> DomainAuthError.ServerError
    RepoAuthError.NetworkError -> DomainAuthError.NetworkError
    is RepoAuthError.UnknownError -> DomainAuthError.UnknownError(this.message)
}

/**
 * Маппер для RegisterError.
 */
fun RepoRegisterError.toDomain() = when (this) {
    RepoRegisterError.InvalidFormat -> DomainRegisterError.InvalidFormat
    RepoRegisterError.UserAlreadyExists -> DomainRegisterError.UserAlreadyExists
    RepoRegisterError.NetworkError -> DomainRegisterError.NetworkError
    RepoRegisterError.ServerError -> DomainRegisterError.ServerError
    is RepoRegisterError.UnknownError -> DomainRegisterError.UnknownError(this.message)
}

/**
 * Маппер для ConfirmationError.
 */
fun RepoConfirmationError.toDomain() = when (this) {
    RepoConfirmationError.InvalidCodeError -> DomainConfirmationError.InvalidCodeError
    RepoConfirmationError.NetworkError -> DomainConfirmationError.NetworkError
    RepoConfirmationError.ServerError -> DomainConfirmationError.ServerError
    is RepoConfirmationError.UnknownError -> DomainConfirmationError.UnknownError(this.message)
}

/**
 * Маппер для ResendConfirmationError.
 */
fun RepoResendConfirmationError.toDomain() = when (this) {
    RepoResendConfirmationError.InvalidPhone -> DomainResendConfirmationError.InvalidPhone
    RepoResendConfirmationError.NetworkError -> DomainResendConfirmationError.NetworkError
    RepoResendConfirmationError.ServerError -> DomainResendConfirmationError.ServerError
    is RepoResendConfirmationError.UnknownError -> DomainResendConfirmationError.UnknownError(this.message)
}

/**
 * Маппер для ResetPasswordError.
 */
fun RepoResetPasswordError.toDomain() = when (this) {
    RepoResetPasswordError.InvalidPhoneError -> DomainResetPasswordError.InvalidPhoneError
    RepoResetPasswordError.UserNotFound -> DomainResetPasswordError.UserNotFound
    RepoResetPasswordError.NetworkError -> DomainResetPasswordError.NetworkError
    RepoResetPasswordError.ServerError -> DomainResetPasswordError.ServerError
    is RepoResetPasswordError.UnknownError -> DomainResetPasswordError.UnknownError(this.message)
}

/**
 * Маппер для GetBalanceError.
 */
fun RepoGetBalanceError.toDomain() = when (this) {
    RepoGetBalanceError.NotAuthorizedError -> DomainGetBalanceError.NotAuthorizedError
    RepoGetBalanceError.NetworkError -> DomainGetBalanceError.NetworkError
    RepoGetBalanceError.ServerError -> DomainGetBalanceError.ServerError
    is RepoGetBalanceError.UnknownError -> DomainGetBalanceError.UnknownError(this.message)
}

/**
 * Маппер для LogoutError.
 */
fun RepoLogoutError.toDomain() = when (this) {
    RepoLogoutError.NotAuthorizedError -> DomainLogoutError.NotAuthorizedError
    RepoLogoutError.NetworkError -> DomainLogoutError.NetworkError
    RepoLogoutError.ServerError -> DomainLogoutError.ServerError
    is RepoLogoutError.UnknownError -> DomainLogoutError.UnknownError(this.message)
}

/**
 * Маппер для ProfileError.
 */
fun RepoProfileError.toDomain() = when (this) {
    RepoProfileError.ValidationError -> DomainProfileError.ValidationError
    RepoProfileError.NotAuthorizedError -> DomainProfileError.NotAuthorizedError
    RepoProfileError.UserNotFound -> DomainProfileError.UserNotFound
    RepoProfileError.NetworkError -> DomainProfileError.NetworkError
    RepoProfileError.ServerError -> DomainProfileError.ServerError
    is RepoProfileError.UnknownError -> DomainProfileError.UnknownError(this.message)
}

/**
 * Маппер для ChangePasswordError.
 */
fun RepoChangePasswordError.toDomain() = when (this) {
    RepoChangePasswordError.ValidationError -> DomainChangePasswordError.ValidationError
    RepoChangePasswordError.NotAuthorizedError -> DomainChangePasswordError.NotAuthorizedError
    RepoChangePasswordError.NetworkError -> DomainChangePasswordError.NetworkError
    RepoChangePasswordError.ServerError -> DomainChangePasswordError.ServerError
    is RepoChangePasswordError.UnknownError -> DomainChangePasswordError.UnknownError(this.message)
}

/**
 * Маппер для DeleteError.
 */
fun RepoDeleteError.toDomain() = when (this) {
    RepoDeleteError.NotAuthorizedError -> DomainDeleteError.NotAuthorizedError
    RepoDeleteError.NetworkError -> DomainDeleteError.NetworkError
    RepoDeleteError.ServerError -> DomainDeleteError.ServerError
    is RepoDeleteError.UnknownError -> DomainDeleteError.UnknownError(this.message)
}

/**
 * Маппер для OperationsError.
 */
fun RepoOperationsError.toDomain() = when (this) {
    RepoOperationsError.ValidationError -> DomainOperationsError.ValidationError
    RepoOperationsError.NotAuthorizedError -> DomainOperationsError.NotAuthorizedError
    RepoOperationsError.UserNotFound -> DomainOperationsError.UserNotFound
    RepoOperationsError.NetworkError -> DomainOperationsError.NetworkError
    RepoOperationsError.ServerError -> DomainOperationsError.ServerError
    is RepoOperationsError.UnknownError -> DomainOperationsError.UnknownError(this.message)
}

/**
 * Универсальный маппер для любого AppResult.
 */
inline fun <T_Repo, T_Domain, E_Repo, E_Domain> RepoAppResult<T_Repo, E_Repo>.toDomain(
    crossinline errorMapper: (E_Repo) -> E_Domain,
    crossinline dataMapper: (T_Repo) -> T_Domain
): DomainAppResult<T_Domain, E_Domain> {
    return when (this) {
        is RepoAppResult.Success -> DomainAppResult.Success(dataMapper(this.data))
        is RepoAppResult.Error -> DomainAppResult.Error(errorMapper(this.error))
    }
}

/**
 * Удобные extension функции для конкретных типов ошибок
 */

fun RepoAppResult<Unit, RepoAuthError>.toDomainAuth(): DomainAppResult<Unit, DomainAuthError> =
    toDomain(RepoAuthError::toDomain) { }

fun RepoAppResult<Unit, RepoRegisterError>.toDomainRegister(): DomainAppResult<Unit, DomainRegisterError> =
    toDomain(RepoRegisterError::toDomain) { }

fun RepoAppResult<Unit, RepoConfirmationError>.toDomainConfirmation(): DomainAppResult<Unit, DomainConfirmationError> =
    toDomain(RepoConfirmationError::toDomain) { }

fun RepoAppResult<Unit, RepoResendConfirmationError>.toDomainResendConfirmation(): DomainAppResult<Unit, DomainResendConfirmationError> =
    toDomain(RepoResendConfirmationError::toDomain) { }

fun RepoAppResult<Unit, RepoResetPasswordError>.toDomainResetPassword(): DomainAppResult<Unit, DomainResetPasswordError> =
    toDomain(RepoResetPasswordError::toDomain) { }

fun RepoAppResult<BalanceResponse, RepoGetBalanceError>.toDomainBalance(): DomainAppResult<Balance, DomainGetBalanceError> =
    toDomain(RepoGetBalanceError::toDomain, BalanceResponse::toDomain)

fun RepoAppResult<Unit, RepoLogoutError>.toDomainLogout(): DomainAppResult<Unit, DomainLogoutError> =
    toDomain(RepoLogoutError::toDomain) { }

fun RepoAppResult<Unit, RepoProfileError>.toDomainProfileUpdate(): DomainAppResult<Unit, DomainProfileError> =
    toDomain(RepoProfileError::toDomain) {}

fun RepoAppResult<ProfileResponse, RepoProfileError>.toDomainProfile(): DomainAppResult<DomainProfileResponse, DomainProfileError> =
    toDomain(RepoProfileError::toDomain, ProfileResponse::toDomain)

fun RepoAppResult<Unit, RepoChangePasswordError>.toDomainChangePassword(): DomainAppResult<Unit, DomainChangePasswordError> =
    toDomain(RepoChangePasswordError::toDomain) {}

fun RepoAppResult<Unit, RepoDeleteError>.toDomainDelete(): DomainAppResult<Unit, DomainDeleteError> =
    toDomain(RepoDeleteError::toDomain) {}

fun RepoAppResult<RepoTransactionsPageResponse, RepoOperationsError>.toDomainOperations(): DomainAppResult<DomainTransactionsPageResponse, DomainOperationsError> =
    toDomain(RepoOperationsError::toDomain, RepoTransactionsPageResponse::toDomain)