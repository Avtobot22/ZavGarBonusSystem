package com.zavgar.system.domain.session.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.datastore.datasource.SessionDataSource
import kotlinx.coroutines.withContext

class DeleteSessionUseCase(
    private val sessionDataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(): Result<Unit> = withContext(dispatcherProvider.io) {
        sessionDataSource.deleteSession()
    }
}
