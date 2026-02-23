package com.zavgar.system.domain.repository

import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.response.Balance
import com.zavgar.system.domain.model.error.GetBalanceError
import com.zavgar.system.domain.model.error.OperationsError
import com.zavgar.system.domain.model.request.TransactionsRequest
import com.zavgar.system.domain.model.response.TransactionsPageResponse

interface LoyaltyRepository {

    suspend fun getBalance(): AppResult<Balance, GetBalanceError>

    suspend fun getOperations(transactionsRequest: TransactionsRequest): AppResult<TransactionsPageResponse, OperationsError>

}