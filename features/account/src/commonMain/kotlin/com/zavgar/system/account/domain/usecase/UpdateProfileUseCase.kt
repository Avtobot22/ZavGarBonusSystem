package com.zavgar.system.account.domain.usecase

import com.zavgar.system.account.domain.error.ProfileError
import com.zavgar.system.account.domain.error.toProfileError
import com.zavgar.system.account.domain.model.ProfileRequest
import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.ProfileRequest as NetworkProfileRequest

class UpdateProfileUseCase(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(profileRequest: ProfileRequest): AppResult<Unit, ProfileError> =
        withContext(dispatcherProvider.io) {
            userProfileService.updateProfile(
                NetworkProfileRequest(
                    name = profileRequest.name,
                    birthDate = profileRequest.birthDate,
                )
            ).toAppResult(Throwable::toProfileError)
        }
}
