package com.zavgar.system.account.domain.usecase

import com.zavgar.system.account.domain.error.DeleteError
import com.zavgar.system.account.domain.error.toDeleteError
import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class DeleteProfileUseCase(
    private val userProfileService: UserProfileService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<Unit, DeleteError> =
        withContext(dispatcherProvider.io) {
            userProfileService.delete().toAppResult(Throwable::toDeleteError)
        }
}
