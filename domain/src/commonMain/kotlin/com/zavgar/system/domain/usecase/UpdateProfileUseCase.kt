package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.model.request.ProfileRequest
import com.zavgar.system.domain.repository.UserProfileRepository

class UpdateProfileUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(profileRequest: ProfileRequest) =
        userProfileRepository.updateProfile(profileRequest)
}