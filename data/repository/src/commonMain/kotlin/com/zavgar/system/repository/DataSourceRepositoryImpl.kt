package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.model.response.Session
import com.zavgar.system.domain.repository.DataSourceRepository
import com.zavgar.system.repository.datasource.SessionDataSource
import com.zavgar.system.repository.mapper.toDomain
import kotlinx.coroutines.withContext

class DataSourceRepositoryImpl(
    private val sessionDataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : DataSourceRepository {

    override suspend fun getSession(): Result<Session> = withContext(dispatcherProvider.io) {
        sessionDataSource.getSession().toDomain()
    }

    override suspend fun deleteSession(): Result<Unit> = withContext(dispatcherProvider.io) {
        sessionDataSource.deleteSession()
    }
}