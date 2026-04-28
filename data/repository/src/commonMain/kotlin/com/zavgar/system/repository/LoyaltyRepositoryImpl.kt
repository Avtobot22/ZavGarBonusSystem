package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.GetBalanceError
import com.zavgar.system.domain.model.error.OperationsError
import com.zavgar.system.domain.model.request.TransactionsRequest
import com.zavgar.system.domain.model.response.Balance
import com.zavgar.system.domain.model.response.TransactionsPageResponse
import com.zavgar.system.domain.repository.LoyaltyRepository
import com.zavgar.system.repository.mapper.toDomainBalance
import com.zavgar.system.repository.mapper.toDomainOperations
import com.zavgar.system.repository.mapper.toGetBalanceError
import com.zavgar.system.repository.mapper.toOperationsError
import com.zavgar.system.repository.mapper.toRepo
import com.zavgar.system.repository.remote.LoyaltyService
import com.zavgar.system.repository.util.toRepoResult
import kotlinx.coroutines.withContext

class LoyaltyRepositoryImpl(
    private val loyaltyService: LoyaltyService,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : LoyaltyRepository {

    override suspend fun getBalance(): AppResult<Balance, GetBalanceError> =
        withContext(dispatcherProvider.io) {
            loyaltyService.getBalance()
                .toRepoResult(Throwable::toGetBalanceError)
                .toDomainBalance()
        }

    override suspend fun getOperations(transactionsRequest: TransactionsRequest): AppResult<TransactionsPageResponse, OperationsError> =
        withContext(dispatcherProvider.io) {
            loyaltyService.getOperations(transactionsRequest.toRepo())
                .toRepoResult(Throwable::toOperationsError)
                .toDomainOperations()
        }
}
