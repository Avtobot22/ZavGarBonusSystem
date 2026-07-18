package com.zavgar.system.datastore.datasource

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BalanceCacheDataSourceImplTest {

    private val dataSource = BalanceCacheDataSourceImpl(InMemoryPreferencesDataStore())

    @Test
    fun `cached balance is visible only to its owner`() = runTest {
        dataSource.saveBalance(owner = "account-a", balance = 750)

        assertEquals(750, assertNotNull(dataSource.getCachedBalance("account-a")).balance)
        assertNull(dataSource.getCachedBalance("account-b"))
    }

    @Test
    fun `clear removes balance and owner metadata`() = runTest {
        dataSource.saveBalance(owner = "account-a", balance = 750)

        dataSource.clearBalance()

        assertNull(dataSource.getCachedBalance("account-a"))
    }
}
