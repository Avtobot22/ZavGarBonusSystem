package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.MonthlyAccrualsError
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetMonthlyAccrualsUseCaseTest {

    private val repository = mockk<ProfileRepository>()
    private val useCase = GetMonthlyAccrualsUseCase(repository)

    @Test
    fun `forwards the monthly accruals amount`() = runTest {
        coEvery { repository.getMonthlyAccruals() } returns AppResult.Success(42)

        val result = useCase()

        assertEquals(AppResult.Success(42), result)
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.getMonthlyAccruals() } returns
            AppResult.Error(MonthlyAccrualsError.ServerError)

        val result = useCase()

        assertEquals(AppResult.Error(MonthlyAccrualsError.ServerError), result)
    }
}
