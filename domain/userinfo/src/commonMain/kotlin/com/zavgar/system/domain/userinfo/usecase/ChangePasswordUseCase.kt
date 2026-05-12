package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.ChangePasswordError
import com.zavgar.system.domain.userinfo.model.ChangePasswordRequest
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class ChangePasswordUseCase(
    private val profileRepository: ProfileRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(request: ChangePasswordRequest): AppResult<Unit, ChangePasswordError> =
        withContext(dispatcherProvider.io) {
            profileRepository.changePassword(
                oldPassword = request.oldPassword,
                newPassword = request.newPassword,
            )
        }
}
