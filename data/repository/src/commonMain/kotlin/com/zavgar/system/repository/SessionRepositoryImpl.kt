package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.coroutines.runSuspendCatching
import com.zavgar.system.datastore.datasource.BalanceCacheDataSource
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.model.Session
import com.zavgar.system.domain.session.repository.SessionRepository
import com.zavgar.system.network.auth.AuthTokenCache
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext

class SessionRepositoryImpl(
    private val dataSource: SessionDataSource,
    private val balanceCacheDataSource: BalanceCacheDataSource,
    private val authTokenCache: AuthTokenCache,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : SessionRepository {

    override suspend fun getSession(): AppResult<Session, SessionError> = withContext(dispatcherProvider.io) {
        dataSource.getSession()
            .map { Session(phone = it.phone, accessToken = it.accessToken, refreshToken = it.refreshToken) }
            .toAppResult { SessionError.NotFound }
    }

    override suspend fun deleteSession(): AppResult<Unit, SessionError> = withContext(dispatcherProvider.io) {
        val deletion = dataSource.deleteSession()
        authTokenCache.clear()
        deletion.fold(
            onSuccess = {
                clearBalance()
                AppResult.Success(Unit)
            },
            onFailure = { AppResult.Error(SessionError.NotFound) },
        )
    }

    override suspend fun deleteSessionIfRefreshTokenMatches(
        refreshToken: String,
    ): AppResult<Boolean, SessionError> = withContext(dispatcherProvider.io) {
        dataSource.deleteSessionIfRefreshTokenMatches(refreshToken).fold(
            onSuccess = { deleted ->
                if (!deleted) return@withContext AppResult.Success(false)

                authTokenCache.clear()
                clearBalance()
                AppResult.Success(true)
            },
            onFailure = { AppResult.Error(SessionError.NotFound) },
        )
    }

    private suspend fun clearBalance() {
        runSuspendCatching {
            balanceCacheDataSource.clearBalance()
        }
    }
}
