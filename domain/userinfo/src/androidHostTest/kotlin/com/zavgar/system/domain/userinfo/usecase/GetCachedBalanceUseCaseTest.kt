package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.model.CachedBalance
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetCachedBalanceUseCaseTest {

    private val repository = mockk<ProfileRepository>()
    private val useCase = GetCachedBalanceUseCase(repository)

    @Test
    fun `forwards the cached balance when present`() = runTest {
        val cached = CachedBalance(balance = 200, updatedAtMillis = 1_700_000_000_000)
        coEvery { repository.getCachedBalance() } returns cached

        val result = useCase()

        assertEquals(cached, result)
    }

    @Test
    fun `returns null when there is no cached balance`() = runTest {
        coEvery { repository.getCachedBalance() } returns null

        val result = useCase()

        assertNull(result)
    }
}
