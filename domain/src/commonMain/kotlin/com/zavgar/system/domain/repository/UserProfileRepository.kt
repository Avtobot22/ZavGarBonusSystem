package com.zavgar.system.domain.repository

import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.response.ProfileResponse
import com.zavgar.system.domain.model.error.ChangePasswordError
import com.zavgar.system.domain.model.error.DeleteError
import com.zavgar.system.domain.model.error.LogoutError
import com.zavgar.system.domain.model.error.ProfileError
import com.zavgar.system.domain.model.request.ChangePasswordRequest
import com.zavgar.system.domain.model.request.ProfileRequest

interface UserProfileRepository {

    suspend fun getProfile(): AppResult<ProfileResponse, ProfileError>

    suspend fun updateProfile(profileRequest: ProfileRequest): AppResult<Unit, ProfileError>

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): AppResult<Unit, ChangePasswordError>

    suspend fun logout(): AppResult<Unit, LogoutError>

    suspend fun delete(): AppResult<Unit, DeleteError>
}