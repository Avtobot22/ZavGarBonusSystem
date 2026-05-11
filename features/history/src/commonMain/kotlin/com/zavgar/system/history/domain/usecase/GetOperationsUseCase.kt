package com.zavgar.system.history.domain.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.history.domain.error.OperationsError
import com.zavgar.system.history.domain.error.toOperationsError
import com.zavgar.system.history.domain.model.OperationType
import com.zavgar.system.history.domain.model.PointsType
import com.zavgar.system.history.domain.model.Transaction
import com.zavgar.system.history.domain.model.TransactionsPageResponse
import com.zavgar.system.history.model.TransactionsRequest
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.OperationType as NetworkOperationType
import com.zavgar.system.network.model.PointsType as NetworkPointsType
import com.zavgar.system.network.model.Transaction as NetworkTransaction
import com.zavgar.system.network.model.TransactionsPageResponse as NetworkTransactionsPageResponse
import com.zavgar.system.network.model.TransactionsRequest as NetworkTransactionsRequest

class GetOperationsUseCase(
    private val loyaltyService: LoyaltyService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(
        request: TransactionsRequest,
    ): AppResult<TransactionsPageResponse, OperationsError> = withContext(dispatcherProvider.io) {
        loyaltyService.getOperations(
            NetworkTransactionsRequest(
                periodStart = request.periodStart,
                periodEnd = request.periodEnd,
                cursor = request.cursor,
                limit = request.limit,
            )
        )
            .map { it.toDomain() }
            .toAppResult(Throwable::toOperationsError)
    }

    private fun NetworkTransactionsPageResponse.toDomain() = TransactionsPageResponse(
        transactions = items.map { it.toDomain() },
        newCursor = nextCursor,
        hasMore = hasMore,
    )

    private fun NetworkTransaction.toDomain() = Transaction(
        id = id,
        operationType = when (operationType) {
            NetworkOperationType.CREDITING -> OperationType.CREDITING
            NetworkOperationType.DEBITING -> OperationType.DEBITING
        },
        date = date,
        store = store,
        amount = amount,
        pointsType = when (pointsType) {
            NetworkPointsType.BONUS -> PointsType.BONUS
            NetworkPointsType.CASHBACK -> PointsType.CASHBACK
        },
    )
}
