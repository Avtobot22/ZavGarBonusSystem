package com.zavgar.system.settings.domain.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.settings.domain.error.LogoutError
import com.zavgar.system.settings.domain.error.toLogoutError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class LogoutUseCase(
    private val userProfileService: UserProfileService,
    private val sessionDataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<Unit, LogoutError> =
        withContext(dispatcherProvider.io) {
            sessionDataSource.deleteSession().onFailure { exception ->
                return@withContext AppResult.Error(
                    LogoutError.UnknownError(exception.message ?: "Storage Error")
                )
            }

            userProfileService.logout().toAppResult(Throwable::toLogoutError)
        }
}
