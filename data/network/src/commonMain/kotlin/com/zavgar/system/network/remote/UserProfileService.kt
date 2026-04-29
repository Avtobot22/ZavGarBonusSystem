package com.zavgar.system.network.remote

import com.zavgar.system.network.model.ChangePasswordRequest
import com.zavgar.system.network.model.ProfileRequest
import com.zavgar.system.network.model.ProfileResponse

interface UserProfileService {

    suspend fun getProfile(): Result<ProfileResponse>

    suspend fun updateProfile(profileRequest: ProfileRequest): Result<Unit>

    suspend fun changePassword(changePasswordRequest: ChangePasswordRequest): Result<Unit>

    suspend fun logout(): Result<Unit>

    suspend fun delete(): Result<Unit>
}
