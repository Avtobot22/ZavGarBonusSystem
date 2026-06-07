package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.domain.auth.model.RegisterRequest
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterUseCaseTest {

    private val repository = mockk<AuthRepository>()
    private val useCase = RegisterUseCase(repository)

    @Test
    fun `passes all request fields to the repository and forwards success`() = runTest {
        val birthDate = LocalDate(1990, 5, 20)
        coEvery { repository.register("John", birthDate, "1234567890") } returns AppResult.Success(Unit)

        val result = useCase(RegisterRequest(name = "John", birthDate = birthDate, phone = "1234567890"))

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.register("John", birthDate, "1234567890") }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.register(any(), any(), any()) } returns
            AppResult.Error(RegisterError.UserAlreadyExists)

        val result = useCase(RegisterRequest("John", LocalDate(2000, 1, 1), "1234567890"))

        assertEquals(AppResult.Error(RegisterError.UserAlreadyExists), result)
    }
}
