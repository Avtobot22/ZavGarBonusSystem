package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.error.toProfileError
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class GetUserProfileUseCase(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<UserProfile, ProfileError> =
        withContext(dispatcherProvider.io) {
            userProfileService.getProfile()
                .map { UserProfile(name = it.name, phone = it.phone, birthDate = it.birthDate) }
                .toAppResult(Throwable::toProfileError)
        }
}
