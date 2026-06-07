package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.domain.auth.model.LoginRequest
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LoginUseCaseTest {

    private val repository = mockk<AuthRepository>()
    private val useCase = LoginUseCase(repository)

    @Test
    fun `delegates the request phone to the repository and forwards success`() = runTest {
        coEvery { repository.login("1234567890") } returns AppResult.Success(Unit)

        val result = useCase(LoginRequest(phone = "1234567890"))

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.login("1234567890") }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.login(any()) } returns AppResult.Error(AuthError.UserNotFound)

        val result = useCase(LoginRequest(phone = "0000000000"))

        assertEquals(AppResult.Error(AuthError.UserNotFound), result)
    }
}
