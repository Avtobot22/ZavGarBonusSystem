package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class LogoutUseCase(
    private val profileRepository: ProfileRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<Unit, LogoutError> =
        withContext(dispatcherProvider.io) {
            profileRepository.logout()
        }
}
