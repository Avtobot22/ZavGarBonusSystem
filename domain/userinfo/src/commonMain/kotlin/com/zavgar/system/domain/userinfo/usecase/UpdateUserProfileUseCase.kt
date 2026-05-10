package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.error.toProfileError
import com.zavgar.system.domain.userinfo.model.UpdateProfileRequest
import com.zavgar.system.network.model.ProfileRequest as NetworkProfileRequest
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class UpdateUserProfileUseCase(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(request: UpdateProfileRequest): AppResult<Unit, ProfileError> =
        withContext(dispatcherProvider.io) {
            userProfileService.updateProfile(
                NetworkProfileRequest(name = request.name, birthDate = request.birthDate)
            ).toAppResult(Throwable::toProfileError)
        }
}
