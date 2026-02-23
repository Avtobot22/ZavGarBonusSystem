package com.zavgar.system.network.remote

import com.zavgar.system.network.mapper.toNetwork
import com.zavgar.system.network.mapper.toRepo
import com.zavgar.system.repository.model.request.TransactionsRequest
import com.zavgar.system.repository.model.response.BalanceResponse
import com.zavgar.system.repository.model.response.TransactionsPageResponse
import com.zavgar.system.repository.remote.LoyaltyService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path
import com.zavgar.system.network.model.BalanceResponse as NetworkBalanceResponse
import com.zavgar.system.network.model.TransactionsPageResponse as NetworkTransactionsPageResponse

class LoyaltyServiceImpl(
    private val client: HttpClient
) : LoyaltyService {

    override suspend fun getBalance(): Result<BalanceResponse> = runCatching {
        val response = client.get {
            url {
                path("users/me/balance")
            }

        }
        response.body<NetworkBalanceResponse>().toRepo()
    }

    override suspend fun getOperations(transactionsRequest: TransactionsRequest): Result<TransactionsPageResponse> =
        runCatching {
            val response = client.post {
                url {
                    path("users/me/operations")
                }

                setBody(transactionsRequest.toNetwork())
            }
            response.body<NetworkTransactionsPageResponse>().toRepo()
        }
}