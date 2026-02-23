package com.zavgar.system.repository.remote

import com.zavgar.system.repository.model.request.ChangePasswordRequest
import com.zavgar.system.repository.model.request.ProfileRequest
import com.zavgar.system.repository.model.response.ProfileResponse

interface UserProfileService {

    suspend fun getProfile(): Result<ProfileResponse>

    suspend fun updateProfile(profileRequest: ProfileRequest): Result<Unit>

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun delete(): Result<Unit>
}