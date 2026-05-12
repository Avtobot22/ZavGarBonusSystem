package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.model.Session
import com.zavgar.system.domain.session.repository.SessionRepository
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class SessionRepositoryImpl(
    private val dataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : SessionRepository {

    override suspend fun getSession(): AppResult<Session, SessionError> = withContext(dispatcherProvider.io) {
        dataSource.getSession()
            .map { Session(phone = it.phone, accessToken = it.accessToken, refreshToken = it.refreshToken) }
            .toAppResult { SessionError.NotFound }

    }

    override suspend fun deleteSession(): AppResult<Unit, SessionError> = withContext(dispatcherProvider.io) {
        dataSource.deleteSession()
            .toAppResult { SessionError.NotFound }
    }
}
