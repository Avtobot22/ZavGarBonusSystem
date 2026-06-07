package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserProfileUseCaseTest {

    private val repository = mockk<ProfileRepository>()
    private val useCase = GetUserProfileUseCase(repository)

    @Test
    fun `forwards the profile returned by the repository`() = runTest {
        val profile = UserProfile(name = "John", phone = "1234567890", birthDate = LocalDate(1990, 1, 1))
        coEvery { repository.getProfile() } returns AppResult.Success(profile)

        val result = useCase()

        assertEquals(AppResult.Success(profile), result)
        coVerify(exactly = 1) { repository.getProfile() }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.getProfile() } returns AppResult.Error(ProfileError.UserNotFound)

        val result = useCase()

        assertEquals(AppResult.Error(ProfileError.UserNotFound), result)
    }
}
