package com.zavgar.system.network.remote

import com.zavgar.system.coroutines.runSuspendCatching
import com.zavgar.system.network.model.AccrualsSumRequest
import com.zavgar.system.network.model.AccrualsSumResponse
import com.zavgar.system.network.model.BalanceResponse
import com.zavgar.system.network.model.TransactionsPageResponse
import com.zavgar.system.network.model.TransactionsRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path

class LoyaltyServiceImpl(
    private val client: HttpClient,
) : LoyaltyService {

    override suspend fun getBalance(): Result<BalanceResponse> = runSuspendCatching {
        val response = client.get {
            url { path("users/me/balance") }
        }
        response.body<BalanceResponse>()
    }

    override suspend fun getOperations(transactionsRequest: TransactionsRequest): Result<TransactionsPageResponse> =
        runSuspendCatching {
            val response = client.post {
                url { path("users/me/operations") }
                setBody(transactionsRequest)
            }
            response.body<TransactionsPageResponse>()
        }

    override suspend fun getAccrualsSum(request: AccrualsSumRequest): Result<AccrualsSumResponse> =
        runSuspendCatching {
            val response = client.post {
                url { path("users/me/accruals/sum") }
                setBody(request)
            }
            response.body<AccrualsSumResponse>()
        }
}
