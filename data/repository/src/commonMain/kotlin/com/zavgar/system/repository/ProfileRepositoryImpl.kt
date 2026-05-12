package com.zavgar.system.repository

import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.userinfo.error.ChangePasswordError
import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.domain.userinfo.error.MonthlyAccrualsError
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError
import com.zavgar.system.network.model.ChangePasswordRequest
import com.zavgar.system.network.model.ProfileRequest
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import kotlinx.datetime.LocalDate

internal class ProfileRepositoryImpl(
    private val userProfileService: UserProfileService,
    private val loyaltyService: LoyaltyService,
    private val sessionDataSource: SessionDataSource,
) : ProfileRepository {

    override suspend fun getProfile(): AppResult<UserProfile, ProfileError> =
        userProfileService.getProfile().fold(
            onSuccess = { AppResult.Success(UserProfile(name = it.name, phone = it.phone, birthDate = it.birthDate)) },
            onFailure = { AppResult.Error(it.toProfileError()) },
        )

    override suspend fun updateProfile(name: String, birthDate: LocalDate): AppResult<Unit, ProfileError> =
        userProfileService.updateProfile(ProfileRequest(name = name, birthDate = birthDate)).fold(
            onSuccess = { AppResult.Success(Unit) },
            onFailure = { AppResult.Error(it.toProfileError()) },
        )

    override suspend fun delete(): AppResult<Unit, DeleteError> =
        userProfileService.delete().fold(
            onSuccess = { AppResult.Success(Unit) },
            onFailure = { AppResult.Error(it.toDeleteError()) },
        )

    override suspend fun getBalance(): AppResult<Balance, GetBalanceError> =
        loyaltyService.getBalance().fold(
            onSuccess = { AppResult.Success(Balance(balance = it.balance)) },
            onFailure = { AppResult.Error(it.toGetBalanceError()) },
        )

    override suspend fun getMonthlyAccruals(): AppResult<Int, MonthlyAccrualsError> =
        loyaltyService.getAccrualsSum().fold(
            onSuccess = { AppResult.Success(it.sum) },
            onFailure = { AppResult.Error(it.toMonthlyAccrualsError()) },
        )

    override suspend fun changePassword(oldPassword: String, newPassword: String): AppResult<Unit, ChangePasswordError> =
        userProfileService.changePassword(
            ChangePasswordRequest(oldPassword = oldPassword, newPassword = newPassword)
        ).fold(
            onSuccess = { AppResult.Success(Unit) },
            onFailure = { AppResult.Error(it.toChangePasswordError()) },
        )

    override suspend fun logout(): AppResult<Unit, LogoutError> {
        sessionDataSource.deleteSession().onFailure { exception ->
            return AppResult.Error(LogoutError.UnknownError(exception.message ?: "Storage Error"))
        }
        return userProfileService.logout().fold(
            onSuccess = { AppResult.Success(Unit) },
            onFailure = { AppResult.Error(it.toLogoutError()) },
        )
    }

    private fun Throwable.toProfileError(): ProfileError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            400 -> ProfileError.ValidationError
            404 -> ProfileError.UserNotFound
            429 -> ProfileError.TooManyRequestError
            else -> ProfileError.UnknownError(kind.message)
        }
        is NetworkErrorKind.Server -> ProfileError.ServerError
        is NetworkErrorKind.Network -> ProfileError.NetworkError
        is NetworkErrorKind.Unknown -> ProfileError.UnknownError(kind.message)
    }

    private fun Throwable.toDeleteError(): DeleteError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            429 -> DeleteError.TooManyRequestError
            else -> DeleteError.UnknownError(kind.message)
        }
        is NetworkErrorKind.Server -> DeleteError.ServerError
        is NetworkErrorKind.Network -> DeleteError.NetworkError
        is NetworkErrorKind.Unknown -> DeleteError.UnknownError(kind.message)
    }

    private fun Throwable.toGetBalanceError(): GetBalanceError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            429 -> GetBalanceError.TooManyRequestError
            else -> GetBalanceError.UnknownError(kind.message)
        }
        is NetworkErrorKind.Server -> GetBalanceError.ServerError
        is NetworkErrorKind.Network -> GetBalanceError.NetworkError
        is NetworkErrorKind.Unknown -> GetBalanceError.UnknownError(kind.message)
    }

    private fun Throwable.toChangePasswordError(): ChangePasswordError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            400 -> ChangePasswordError.ValidationError
            429 -> ChangePasswordError.TooManyRequestError
            else -> ChangePasswordError.UnknownError(kind.message)
        }
        is NetworkErrorKind.Server -> ChangePasswordError.ServerError
        is NetworkErrorKind.Network -> ChangePasswordError.NetworkError
        is NetworkErrorKind.Unknown -> ChangePasswordError.UnknownError(kind.message)
    }

    private fun Throwable.toMonthlyAccrualsError(): MonthlyAccrualsError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> MonthlyAccrualsError.UnknownError(kind.message)
        is NetworkErrorKind.Server -> MonthlyAccrualsError.ServerError
        is NetworkErrorKind.Network -> MonthlyAccrualsError.NetworkError
        is NetworkErrorKind.Unknown -> MonthlyAccrualsError.UnknownError(kind.message)
    }

    private fun Throwable.toLogoutError(): LogoutError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            429 -> LogoutError.TooManyRequestError
            else -> LogoutError.UnknownError(kind.message)
        }
        is NetworkErrorKind.Server -> LogoutError.ServerError
        is NetworkErrorKind.Network -> LogoutError.NetworkError
        is NetworkErrorKind.Unknown -> LogoutError.UnknownError(kind.message)
    }
}
