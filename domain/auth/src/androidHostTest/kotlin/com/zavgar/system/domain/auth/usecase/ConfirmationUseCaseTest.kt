package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.model.ConfirmationRequest
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmationUseCaseTest {

    private val repository = mockk<AuthRepository>()
    private val useCase = ConfirmationUseCase(repository)

    @Test
    fun `routes registration confirmation to confirmRegistration`() = runTest {
        coEvery { repository.confirmRegistration("1234567890", "1111") } returns AppResult.Success(Unit)

        val result = useCase(
            ConfirmationRequest(phone = "1234567890", code = "1111", isRegistration = true),
        )

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.confirmRegistration("1234567890", "1111") }
        coVerify(exactly = 0) { repository.confirmLogin(any(), any()) }
    }

    @Test
    fun `routes login confirmation to confirmLogin`() = runTest {
        coEvery { repository.confirmLogin("1234567890", "2222") } returns AppResult.Success(Unit)

        val result = useCase(
            ConfirmationRequest(phone = "1234567890", code = "2222", isRegistration = false),
        )

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.confirmLogin("1234567890", "2222") }
        coVerify(exactly = 0) { repository.confirmRegistration(any(), any()) }
    }

    @Test
    fun `forwards an invalid-code error from the chosen repository call`() = runTest {
        coEvery { repository.confirmLogin(any(), any()) } returns
            AppResult.Error(ConfirmationError.InvalidCodeError)

        val result = useCase(
            ConfirmationRequest(phone = "1234567890", code = "0000", isRegistration = false),
        )

        assertEquals(AppResult.Error(ConfirmationError.InvalidCodeError), result)
    }
}
