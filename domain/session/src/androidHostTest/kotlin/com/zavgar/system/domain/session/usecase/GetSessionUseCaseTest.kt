package com.zavgar.system.domain.session.usecase

import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.model.Session
import com.zavgar.system.domain.session.repository.SessionRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetSessionUseCaseTest {

    private val repository = mockk<SessionRepository>()
    private val useCase = GetSessionUseCase(repository)

    @Test
    fun `forwards the session returned by the repository`() = runTest {
        val session = Session(phone = "1234567890", accessToken = "access", refreshToken = "refresh")
        coEvery { repository.getSession() } returns AppResult.Success(session)

        val result = useCase()

        assertEquals(AppResult.Success(session), result)
        coVerify(exactly = 1) { repository.getSession() }
    }

    @Test
    fun `forwards a not-found error unchanged`() = runTest {
        coEvery { repository.getSession() } returns AppResult.Error(SessionError.NotFound)

        val result = useCase()

        assertEquals(AppResult.Error(SessionError.NotFound), result)
    }
}
