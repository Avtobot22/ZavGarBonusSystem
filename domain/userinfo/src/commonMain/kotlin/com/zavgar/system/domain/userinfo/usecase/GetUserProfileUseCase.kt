package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult

class GetUserProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): AppResult<UserProfile, ProfileError> =
        profileRepository.getProfile()
}
