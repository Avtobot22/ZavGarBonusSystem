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
import kotlinx.coroutines.withContext
import com.zavgar.system.repository.model.AppResult as RepoAppResult

class LoyaltyRepositoryImpl(
    private val loyaltyService: LoyaltyService,
    private val dispatcherProvider: CoroutineDispatcherProvider
) : LoyaltyRepository {

    override suspend fun getBalance(): AppResult<Balance, GetBalanceError> =
        withContext(dispatcherProvider.io) {
            val apiResult = loyaltyService.getBalance()

            val apiResponse = apiResult.getOrElse { exception ->
                return@withContext RepoAppResult.Error(exception.toGetBalanceError())
                    .toDomainBalance()
            }

            RepoAppResult.Success(apiResponse).toDomainBalance()
        }

    override suspend fun getOperations(transactionsRequest: TransactionsRequest): AppResult<TransactionsPageResponse, OperationsError> =
        withContext(dispatcherProvider.io) {

            val apiResult = loyaltyService.getOperations(transactionsRequest.toRepo())

            val response = apiResult.getOrElse { exception ->
                return@withContext RepoAppResult.Error(exception.toOperationsError())
                    .toDomainOperations()
            }

            RepoAppResult.Success(response).toDomainOperations()
        }
}