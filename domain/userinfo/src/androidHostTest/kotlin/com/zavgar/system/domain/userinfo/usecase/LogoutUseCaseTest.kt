package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LogoutUseCaseTest {

    private val repository = mockk<ProfileRepository>()
    private val useCase = LogoutUseCase(repository)

    @Test
    fun `delegates logout and forwards success`() = runTest {
        coEvery { repository.logout() } returns AppResult.Success(Unit)

        val result = useCase()

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.logout() }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.logout() } returns AppResult.Error(LogoutError.NetworkError)

        val result = useCase()

        assertEquals(AppResult.Error(LogoutError.NetworkError), result)
    }
}
