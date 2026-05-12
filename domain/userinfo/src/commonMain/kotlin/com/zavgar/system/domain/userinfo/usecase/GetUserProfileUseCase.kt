package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class GetUserProfileUseCase(
    private val profileRepository: ProfileRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<UserProfile, ProfileError> =
        withContext(dispatcherProvider.io) {
            profileRepository.getProfile()
        }
}
