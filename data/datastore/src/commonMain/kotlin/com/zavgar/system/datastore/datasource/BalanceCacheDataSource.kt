package com.zavgar.system.datastore.datasource

import com.zavgar.system.datastore.model.CachedBalance

interface BalanceCacheDataSource {

    suspend fun getCachedBalance(): CachedBalance?

    suspend fun saveBalance(balance: Int)
}
