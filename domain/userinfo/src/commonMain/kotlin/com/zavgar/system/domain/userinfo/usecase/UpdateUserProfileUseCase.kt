package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UpdateProfileRequest
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult

class UpdateUserProfileUseCase(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(request: UpdateProfileRequest): AppResult<Unit, ProfileError> =
        profileRepository.updateProfile(name = request.name, birthDate = request.birthDate)
}
