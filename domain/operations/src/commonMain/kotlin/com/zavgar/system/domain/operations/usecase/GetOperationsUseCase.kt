package com.zavgar.system.domain.operations.usecase

import com.zavgar.system.domain.operations.OperationsRepository
import com.zavgar.system.domain.operations.model.TransactionsRequest

class GetOperationsUseCase(
    private val operationsRepository: OperationsRepository,
) {
    suspend operator fun invoke(request: TransactionsRequest) =
        operationsRepository.getOperations(request)
}
