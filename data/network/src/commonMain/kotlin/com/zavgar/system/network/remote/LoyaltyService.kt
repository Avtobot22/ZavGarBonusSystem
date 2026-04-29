package com.zavgar.system.network.remote

import com.zavgar.system.network.model.BalanceResponse
import com.zavgar.system.network.model.TransactionsPageResponse
import com.zavgar.system.network.model.TransactionsRequest

interface LoyaltyService {

    suspend fun getBalance(): Result<BalanceResponse>

    suspend fun getOperations(transactionsRequest: TransactionsRequest): Result<TransactionsPageResponse>
}
