package com.zavgar.system.repository.remote

import com.zavgar.system.repository.model.request.TransactionsRequest
import com.zavgar.system.repository.model.response.BalanceResponse
import com.zavgar.system.repository.model.response.TransactionsPageResponse

interface LoyaltyService {

    suspend fun getBalance(): Result<BalanceResponse>

    suspend fun getOperations(transactionsRequest: TransactionsRequest): Result<TransactionsPageResponse>
}