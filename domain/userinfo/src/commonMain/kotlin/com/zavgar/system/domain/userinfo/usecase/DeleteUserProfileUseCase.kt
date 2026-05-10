package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.error.toDeleteError
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class DeleteUserProfileUseCase(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<Unit, DeleteError> =
        withContext(dispatcherProvider.io) {
            userProfileService.delete().toAppResult(Throwable::toDeleteError)
        }
}
