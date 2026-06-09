package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.datastore.datasource.BalanceCacheDataSource
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.domain.userinfo.error.MonthlyAccrualsError
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.model.CachedBalance
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError
import com.zavgar.system.network.mapper.isTooManyRequests
import com.zavgar.system.network.model.ApiErrorCode
import com.zavgar.system.network.model.ProfileRequest
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

internal class ProfileRepositoryImpl(
    private val userProfileService: UserProfileService,
    private val loyaltyService: LoyaltyService,
    private val sessionDataSource: SessionDataSource,
    private val balanceCacheDataSource: BalanceCacheDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : ProfileRepository {

    override suspend fun getProfile(): AppResult<UserProfile, ProfileError> = withContext(dispatcherProvider.io) {
        userProfileService.getProfile().fold(
            onSuccess = { AppResult.Success(UserProfile(name = it.name, phone = it.phone, birthDate = it.birthDate)) },
            onFailure = { AppResult.Error(it.toProfileError()) },
        )
    }

    override suspend fun updateProfile(name: String, birthDate: LocalDate): AppResult<Unit, ProfileError> =
        withContext(dispatcherProvider.io) {
            userProfileService.updateProfile(ProfileRequest(name = name, birthDate = birthDate)).fold(
                onSuccess = { AppResult.Success(Unit) },
                onFailure = { AppResult.Error(it.toProfileError()) },
            )
        }

    override suspend fun delete(): AppResult<Unit, DeleteError> = withContext(dispatcherProvider.io) {
        val serverResult = userProfileService.delete()
        val sessionResult = sessionDataSource.deleteSession()

        serverResult.onFailure { serverError ->
            return@withContext AppResult.Error(serverError.toDeleteError())
        }
        sessionResult.onFailure { exception ->
            return@withContext AppResult.Error(
                DeleteError.UnknownError(exception.message ?: "Local storage cleanup failed"),
            )
        }
        AppResult.Success(Unit)
    }

    override suspend fun getBalance(): AppResult<Balance, GetBalanceError> = withContext(dispatcherProvider.io) {
        loyaltyService.getBalance().fold(
            onSuccess = {
                balanceCacheDataSource.saveBalance(it.balance)
                AppResult.Success(Balance(balance = it.balance))
            },
            onFailure = { AppResult.Error(it.toGetBalanceError()) },
        )
    }

    override suspend fun getCachedBalance(): CachedBalance? = withContext(dispatcherProvider.io) {
        balanceCacheDataSource.getCachedBalance()?.let {
            CachedBalance(balance = it.balance, updatedAtMillis = it.updatedAtMillis)
        }
    }

    override suspend fun getMonthlyAccruals(): AppResult<Int, MonthlyAccrualsError> =
        withContext(dispatcherProvider.io) {
            loyaltyService.getAccrualsSum().fold(
                onSuccess = { AppResult.Success(it.sum) },
                onFailure = { AppResult.Error(it.toMonthlyAccrualsError()) },
            )
        }

    override suspend fun logout(): AppResult<Unit, LogoutError> = withContext(dispatcherProvider.io) {
        val serverResult = userProfileService.logout()
        val sessionResult = sessionDataSource.deleteSession()

        serverResult.onFailure { serverError ->
            return@withContext AppResult.Error(serverError.toLogoutError())
        }
        sessionResult.onFailure { exception ->
            return@withContext AppResult.Error(
                LogoutError.UnknownError(exception.message ?: "Local session deletion failed"),
            )
        }

        AppResult.Success(Unit)
    }

    private fun Throwable.toProfileError(): ProfileError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> kind.toProfileError()
        is NetworkErrorKind.Server -> ProfileError.ServerError
        is NetworkErrorKind.Network -> ProfileError.NetworkError
        is NetworkErrorKind.Unknown -> ProfileError.UnknownError(kind.message)
    }

    private fun NetworkErrorKind.Client.toProfileError(): ProfileError = when (errorCode) {
        ApiErrorCode.NOT_FOUND -> ProfileError.UserNotFound
        ApiErrorCode.TOO_MANY_REQUESTS -> ProfileError.TooManyRequestError(retryAfterSeconds)
        ApiErrorCode.VALIDATION_ERROR,
        ApiErrorCode.INVALID_FORMAT,
        ApiErrorCode.INVALID_ARGUMENT,
        -> ProfileError.ValidationError

        else -> when (statusCode) {
            HttpStatusCodes.NOT_FOUND -> ProfileError.UserNotFound
            HttpStatusCodes.BAD_REQUEST -> ProfileError.ValidationError
            HttpStatusCodes.TOO_MANY_REQUESTS -> ProfileError.TooManyRequestError(retryAfterSeconds)
            else -> ProfileError.UnknownError(message)
        }
    }

    private fun Throwable.toDeleteError(): DeleteError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when {
            kind.isTooManyRequests() -> DeleteError.TooManyRequestError(kind.retryAfterSeconds)
            else -> DeleteError.UnknownError(kind.message)
        }

        is NetworkErrorKind.Server -> DeleteError.ServerError
        is NetworkErrorKind.Network -> DeleteError.NetworkError
        is NetworkErrorKind.Unknown -> DeleteError.UnknownError(kind.message)
    }

    private fun Throwable.toGetBalanceError(): GetBalanceError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when {
            kind.isTooManyRequests() -> GetBalanceError.TooManyRequestError(kind.retryAfterSeconds)
            else -> GetBalanceError.UnknownError(kind.message)
        }

        is NetworkErrorKind.Server -> GetBalanceError.ServerError
        is NetworkErrorKind.Network -> GetBalanceError.NetworkError
        is NetworkErrorKind.Unknown -> GetBalanceError.UnknownError(kind.message)
    }

    private fun Throwable.toMonthlyAccrualsError(): MonthlyAccrualsError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> MonthlyAccrualsError.UnknownError(kind.message)
        is NetworkErrorKind.Server -> MonthlyAccrualsError.ServerError
        is NetworkErrorKind.Network -> MonthlyAccrualsError.NetworkError
        is NetworkErrorKind.Unknown -> MonthlyAccrualsError.UnknownError(kind.message)
    }

    private fun Throwable.toLogoutError(): LogoutError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when {
            kind.isTooManyRequests() -> LogoutError.TooManyRequestError(kind.retryAfterSeconds)
            else -> LogoutError.UnknownError(kind.message)
        }

        is NetworkErrorKind.Server -> LogoutError.ServerError
        is NetworkErrorKind.Network -> LogoutError.NetworkError
        is NetworkErrorKind.Unknown -> LogoutError.UnknownError(kind.message)
    }
}
