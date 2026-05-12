package com.zavgar.system.domain.session.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.session.Session
import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class GetSessionUseCase(
    private val sessionDataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): AppResult<Session, SessionError> = withContext(dispatcherProvider.io) {
        sessionDataSource.getSession()
            .map { Session(phone = it.phone, accessToken = it.accessToken, refreshToken = it.refreshToken) }
            .toAppResult { SessionError.UnknownError(it.message ?: "Unknown") }
    }
}
