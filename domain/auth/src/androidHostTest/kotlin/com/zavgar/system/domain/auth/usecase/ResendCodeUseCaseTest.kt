package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.domain.auth.model.ResendRequest
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ResendCodeUseCaseTest {

    private val repository = mockk<AuthRepository>()
    private val useCase = ResendCodeUseCase(repository)

    @Test
    fun `delegates the request phone to the repository and forwards success`() = runTest {
        coEvery { repository.resendCode("1234567890") } returns AppResult.Success(Unit)

        val result = useCase(ResendRequest(phone = "1234567890"))

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.resendCode("1234567890") }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.resendCode(any()) } returns
            AppResult.Error(ResendConfirmationError.TooManyRequestError)

        val result = useCase(ResendRequest(phone = "1234567890"))

        assertEquals(AppResult.Error(ResendConfirmationError.TooManyRequestError), result)
    }
}
