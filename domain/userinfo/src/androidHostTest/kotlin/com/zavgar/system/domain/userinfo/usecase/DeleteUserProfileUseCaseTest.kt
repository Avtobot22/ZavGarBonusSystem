package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteUserProfileUseCaseTest {

    private val repository = mockk<ProfileRepository>()
    private val useCase = DeleteUserProfileUseCase(repository)

    @Test
    fun `delegates deletion and forwards success`() = runTest {
        coEvery { repository.delete() } returns AppResult.Success(Unit)

        val result = useCase()

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.delete() }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.delete() } returns AppResult.Error(DeleteError.ServerError)

        val result = useCase()

        assertEquals(AppResult.Error(DeleteError.ServerError), result)
    }
}
