package com.zavgar.system.domain.session.usecase

import com.zavgar.system.domain.session.repository.SessionRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteSessionIfCurrentUseCaseTest {

    private val repository = mockk<SessionRepository>()
    private val useCase = DeleteSessionIfCurrentUseCase(repository)

    @Test
    fun `delegates conditional deletion to repository`() = runTest {
        coEvery { repository.deleteSessionIfRefreshTokenMatches("refresh") } returns AppResult.Success(true)

        assertEquals(AppResult.Success(true), useCase("refresh"))
        coVerify(exactly = 1) { repository.deleteSessionIfRefreshTokenMatches("refresh") }
    }
}
