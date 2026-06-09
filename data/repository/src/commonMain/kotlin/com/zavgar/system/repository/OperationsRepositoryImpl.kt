package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.operations.OperationsRepository
import com.zavgar.system.domain.operations.error.OperationsError
import com.zavgar.system.domain.operations.model.OperationType
import com.zavgar.system.domain.operations.model.PointsType
import com.zavgar.system.domain.operations.model.SortOrder
import com.zavgar.system.domain.operations.model.Transaction
import com.zavgar.system.domain.operations.model.TransactionsPageResponse
import com.zavgar.system.domain.operations.model.TransactionsRequest
import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError
import com.zavgar.system.network.model.ApiErrorCode
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.OperationType as NetworkOperationType
import com.zavgar.system.network.model.PointsType as NetworkPointsType
import com.zavgar.system.network.model.SortOrder as NetworkSortOrder
import com.zavgar.system.network.model.Transaction as NetworkTransaction
import com.zavgar.system.network.model.TransactionsPageResponse as NetworkTransactionsPageResponse
import com.zavgar.system.network.model.TransactionsRequest as NetworkTransactionsRequest

internal class OperationsRepositoryImpl(
    private val loyaltyService: LoyaltyService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : OperationsRepository {

    override suspend fun getOperations(
        request: TransactionsRequest,
    ): AppResult<TransactionsPageResponse, OperationsError> =
        withContext(dispatcherProvider.io) {
            loyaltyService.getOperations(
                NetworkTransactionsRequest(
                    periodStart = request.periodStart,
                    periodEnd = request.periodEnd,
                    cursor = request.cursor,
                    limit = request.limit,
                    sortOrder = request.sortOrder.toNetwork(),
                ),
            ).fold(
                onSuccess = { AppResult.Success(it.toDomain()) },
                onFailure = { AppResult.Error(it.toOperationsError()) },
            )
        }

    private fun NetworkTransactionsPageResponse.toDomain() = TransactionsPageResponse(
        transactions = items.map { it.toDomain() },
        newCursor = nextCursor,
        hasMore = hasMore,
    )

    private fun SortOrder.toNetwork(): NetworkSortOrder = when (this) {
        SortOrder.ASC -> NetworkSortOrder.ASC
        SortOrder.DESC -> NetworkSortOrder.DESC
    }

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

    private fun Throwable.toOperationsError(): OperationsError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> kind.toOperationsError()
        is NetworkErrorKind.Server -> OperationsError.ServerError
        is NetworkErrorKind.Network -> OperationsError.NetworkError
        is NetworkErrorKind.Unknown -> OperationsError.UnknownError(kind.message)
    }

    private fun NetworkErrorKind.Client.toOperationsError(): OperationsError = when (errorCode) {
        ApiErrorCode.NOT_FOUND -> OperationsError.UserNotFound
        ApiErrorCode.TOO_MANY_REQUESTS -> OperationsError.TooManyRequestError(retryAfterSeconds)
        ApiErrorCode.VALIDATION_ERROR,
        ApiErrorCode.INVALID_FORMAT,
        ApiErrorCode.INVALID_ARGUMENT,
        -> OperationsError.ValidationError

        else -> when (statusCode) {
            HttpStatusCodes.NOT_FOUND -> OperationsError.UserNotFound
            HttpStatusCodes.BAD_REQUEST -> OperationsError.ValidationError
            HttpStatusCodes.TOO_MANY_REQUESTS -> OperationsError.TooManyRequestError(retryAfterSeconds)
            else -> OperationsError.UnknownError(message)
        }
    }
}
