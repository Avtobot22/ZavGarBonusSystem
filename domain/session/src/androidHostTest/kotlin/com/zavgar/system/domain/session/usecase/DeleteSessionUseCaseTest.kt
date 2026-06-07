package com.zavgar.system.domain.session.usecase

import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.repository.SessionRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteSessionUseCaseTest {

    private val repository = mockk<SessionRepository>()
    private val useCase = DeleteSessionUseCase(repository)

    @Test
    fun `delegates deletion and forwards success`() = runTest {
        coEvery { repository.deleteSession() } returns AppResult.Success(Unit)

        val result = useCase()

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.deleteSession() }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.deleteSession() } returns
            AppResult.Error(SessionError.UnknownError("boom"))

        val result = useCase()

        assertEquals(AppResult.Error(SessionError.UnknownError("boom")), result)
    }
}
