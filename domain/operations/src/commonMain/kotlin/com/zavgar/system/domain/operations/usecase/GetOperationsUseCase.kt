package com.zavgar.system.domain.operations.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.operations.OperationsRepository
import com.zavgar.system.domain.operations.error.OperationsError
import com.zavgar.system.domain.operations.model.TransactionsPageResponse
import com.zavgar.system.domain.operations.model.TransactionsRequest
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class GetOperationsUseCase(
    private val operationsRepository: OperationsRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(
        request: TransactionsRequest,
    ): AppResult<TransactionsPageResponse, OperationsError> =
        withContext(dispatcherProvider.io) {
            operationsRepository.getOperations(request)
        }
}
