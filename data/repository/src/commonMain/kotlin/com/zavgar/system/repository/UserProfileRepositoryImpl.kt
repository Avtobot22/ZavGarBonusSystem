package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.response.ProfileResponse
import com.zavgar.system.domain.model.error.ChangePasswordError
import com.zavgar.system.domain.model.error.DeleteError
import com.zavgar.system.domain.model.error.LogoutError
import com.zavgar.system.domain.model.error.ProfileError
import com.zavgar.system.domain.model.request.ChangePasswordRequest
import com.zavgar.system.domain.model.request.ProfileRequest
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
import kotlinx.coroutines.withContext
import com.zavgar.system.repository.model.AppResult as RepoAppResult
import com.zavgar.system.repository.model.error.ChangePasswordError as RepoChangePasswordError
import com.zavgar.system.repository.model.error.DeleteError as RepoDeleteError
import com.zavgar.system.repository.model.error.LogoutError as RepoLogoutError
import com.zavgar.system.repository.model.error.ProfileError as RepoProfileError
import com.zavgar.system.repository.model.response.ProfileResponse as RepoProfileResponse


class UserProfileRepositoryImpl(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
    private val sessionDataSource: SessionDataSource
) : UserProfileRepository {
    override suspend fun getProfile(): AppResult<ProfileResponse, ProfileError> = withContext(dispatcherProvider.io) {

        val apiResult = userProfileService.getProfile()

        val response = apiResult.getOrElse { exception ->
            val errorResult: RepoAppResult<RepoProfileResponse, RepoProfileError> =
                RepoAppResult.Error(exception.toProfileError())
            return@withContext errorResult.toDomainProfile()
        }

        RepoAppResult.Success(response).toDomainProfile()
    }

    override suspend fun updateProfile(profileRequest: ProfileRequest): AppResult<Unit, ProfileError> =
        withContext(dispatcherProvider.io) {

            val apiResult = userProfileService.updateProfile(profileRequest.toRepo())

            val response = apiResult.getOrElse { exception ->
                val errorResult: RepoAppResult<Unit, RepoProfileError> =
                    RepoAppResult.Error(exception.toProfileError())
                return@withContext errorResult.toDomainProfileUpdate()
            }

            RepoAppResult.Success(response).toDomainProfileUpdate()
        }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): AppResult<Unit, ChangePasswordError> =
        withContext(dispatcherProvider.io) {

            val apiResult = userProfileService.changePassword(changePasswordRequest.toRepo())

            val response = apiResult.getOrElse { exception ->
                val errorResult: RepoAppResult<Unit, RepoChangePasswordError> =
                    RepoAppResult.Error(exception.toChangePasswordError())
                return@withContext errorResult.toDomainChangePassword()
            }

            RepoAppResult.Success(response).toDomainChangePassword()
        }

    override suspend fun logout(): AppResult<Unit, LogoutError> = withContext(dispatcherProvider.io) {

        sessionDataSource.deleteSession()
            .onFailure { exception ->
                return@withContext RepoAppResult.Error(
                    RepoLogoutError.UnknownError(exception.message ?: "Storage Error")
                ).toDomainLogout()
            }

        val apiResult = userProfileService.logout()

        val response = apiResult.getOrElse { exception ->
            return@withContext RepoAppResult.Error(exception.toLogoutError())
                .toDomainLogout()
        }

        RepoAppResult.Success(response).toDomainLogout()
    }

    override suspend fun delete(): AppResult<Unit, DeleteError> = withContext(dispatcherProvider.io) {

        sessionDataSource.deleteSession()
            .onFailure { exception ->
                return@withContext RepoAppResult.Error(
                    RepoDeleteError.UnknownError(exception.message ?: "Storage Error")
                ).toDomainDelete()
            }

        val apiResult = userProfileService.delete()

        val response = apiResult.getOrElse { exception ->
            return@withContext RepoAppResult.Error(exception.toDeleteError())
                .toDomainDelete()
        }

        RepoAppResult.Success(response).toDomainDelete()
    }
}