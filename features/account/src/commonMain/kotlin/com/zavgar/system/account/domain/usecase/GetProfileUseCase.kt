package com.zavgar.system.account.domain.usecase

import com.zavgar.system.account.domain.error.ProfileError
import com.zavgar.system.account.domain.error.toProfileError
import com.zavgar.system.account.domain.model.ProfileResponse
import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class GetProfileUseCase(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<ProfileResponse, ProfileError> =
        withContext(dispatcherProvider.io) {
            userProfileService.getProfile()
                .map { ProfileResponse(name = it.name, birthDate = it.birthDate) }
                .toAppResult(Throwable::toProfileError)
        }
}
