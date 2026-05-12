package com.zavgar.system.domain.operations

import com.zavgar.system.domain.operations.error.OperationsError
import com.zavgar.system.domain.operations.model.TransactionsPageResponse
import com.zavgar.system.domain.operations.model.TransactionsRequest
import com.zavgar.system.utils.result.AppResult

interface OperationsRepository {
    suspend fun getOperations(request: TransactionsRequest): AppResult<TransactionsPageResponse, OperationsError>
}
