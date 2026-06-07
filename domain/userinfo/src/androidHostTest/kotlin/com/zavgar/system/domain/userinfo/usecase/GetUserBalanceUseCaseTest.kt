package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserBalanceUseCaseTest {

    private val repository = mockk<ProfileRepository>()
    private val useCase = GetUserBalanceUseCase(repository)

    @Test
    fun `forwards the balance returned by the repository`() = runTest {
        coEvery { repository.getBalance() } returns AppResult.Success(Balance(balance = 1500))

        val result = useCase()

        assertEquals(AppResult.Success(Balance(1500)), result)
        coVerify(exactly = 1) { repository.getBalance() }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.getBalance() } returns AppResult.Error(GetBalanceError.NetworkError)

        val result = useCase()

        assertEquals(AppResult.Error(GetBalanceError.NetworkError), result)
    }
}
