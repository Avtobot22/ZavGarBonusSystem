package com.zavgar.system.datastore.datasource

import com.zavgar.system.datastore.model.CachedBalance

interface BalanceCacheDataSource {

    suspend fun getCachedBalance(owner: String): CachedBalance?

    suspend fun saveBalance(owner: String, balance: Int)

    suspend fun clearBalance()
}
