package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UpdateProfileRequest
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class UpdateUserProfileUseCase(
    private val profileRepository: ProfileRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(request: UpdateProfileRequest): AppResult<Unit, ProfileError> =
        withContext(dispatcherProvider.io) {
            profileRepository.updateProfile(name = request.name, birthDate = request.birthDate)
        }
}
