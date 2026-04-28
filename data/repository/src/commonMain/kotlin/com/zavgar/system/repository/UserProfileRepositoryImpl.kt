package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.ChangePasswordError
import com.zavgar.system.domain.model.error.DeleteError
import com.zavgar.system.domain.model.error.LogoutError
import com.zavgar.system.domain.model.error.ProfileError
import com.zavgar.system.domain.model.request.ChangePasswordRequest
import com.zavgar.system.domain.model.request.ProfileRequest
import com.zavgar.system.domain.model.response.ProfileResponse
import com.zavgar.system.domain.repository.UserProfileRepository
import com.zavgar.system.repository.datasource.SessionDataSource
import com.zavgar.system.repository.mapper.toChangePasswordError
import com.zavgar.system.repository.mapper.toDeleteError
import com.zavgar.system.repository.mapper.toDomainChangePassword
import com.zavgar.system.repository.mapper.toDomainDelete
import com.zavgar.system.repository.mapper.toDomainLogout
import com.zavgar.system.repository.mapper.toDomainProfile
import com.zavgar.system.repository.mapper.toDomainProfileUpdate
import com.zavgar.system.repository.mapper.toLogoutError
import com.zavgar.system.repository.mapper.toProfileError
import com.zavgar.system.repository.mapper.toRepo
import com.zavgar.system.repository.remote.UserProfileService
import com.zavgar.system.repository.util.toRepoResult
import kotlinx.coroutines.withContext

class UserProfileRepositoryImpl(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val sessionDataSource: SessionDataSource
) : UserProfileRepository {
    override suspend fun getProfile(): AppResult<ProfileResponse, ProfileError> =
        withContext(dispatcherProvider.io) {
            userProfileService.getProfile()
                .toRepoResult(Throwable::toProfileError)
                .toDomainProfile()
        }

    override suspend fun updateProfile(profileRequest: ProfileRequest): AppResult<Unit, ProfileError> =
        withContext(dispatcherProvider.io) {
            userProfileService.updateProfile(profileRequest.toRepo())
                .toRepoResult(Throwable::toProfileError)
                .toDomainProfileUpdate()
        }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): AppResult<Unit, ChangePasswordError> =
        withContext(dispatcherProvider.io) {
            userProfileService.changePassword(changePasswordRequest.toRepo())
                .toRepoResult(Throwable::toChangePasswordError)
                .toDomainChangePassword()
        }

    override suspend fun logout(): AppResult<Unit, LogoutError> =
        withContext(dispatcherProvider.io) {
            sessionDataSource.deleteSession()
                .onFailure { exception ->
                    return@withContext AppResult.Error(
                        LogoutError.UnknownError(exception.message ?: "Storage Error")
                    )
                }

            userProfileService.logout()
                .toRepoResult(Throwable::toLogoutError)
                .toDomainLogout()
        }

    override suspend fun delete(): AppResult<Unit, DeleteError> =
        withContext(dispatcherProvider.io) {
            sessionDataSource.deleteSession()
                .onFailure { exception ->
                    return@withContext AppResult.Error(
                        DeleteError.UnknownError(exception.message ?: "Storage Error")
                    )
                }

            userProfileService.delete()
                .toRepoResult(Throwable::toDeleteError)
                .toDomainDelete()
        }
}
