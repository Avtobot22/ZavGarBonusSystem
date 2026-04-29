package com.zavgar.system.account.domain.usecase

import com.zavgar.system.account.domain.error.ChangePasswordError
import com.zavgar.system.account.domain.error.toChangePasswordError
import com.zavgar.system.account.domain.model.ChangePasswordRequest
import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.ChangePasswordRequest as NetworkChangePasswordRequest

class ChangePasswordUseCase(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(changePasswordRequest: ChangePasswordRequest): AppResult<Unit, ChangePasswordError> =
        withContext(dispatcherProvider.io) {
            userProfileService.changePassword(
                NetworkChangePasswordRequest(
                    oldPassword = changePasswordRequest.oldPassword,
                    newPassword = changePasswordRequest.newPassword,
                )
            ).toAppResult(Throwable::toChangePasswordError)
        }
}
