package com.zavgar.system.domain.userinfo.usecase

import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UpdateProfileRequest
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateUserProfileUseCaseTest {

    private val repository = mockk<ProfileRepository>()
    private val useCase = UpdateUserProfileUseCase(repository)

    @Test
    fun `passes name and birthDate to the repository and forwards success`() = runTest {
        val birthDate = LocalDate(1985, 3, 10)
        coEvery { repository.updateProfile("Jane", birthDate) } returns AppResult.Success(Unit)

        val result = useCase(UpdateProfileRequest(name = "Jane", birthDate = birthDate))

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { repository.updateProfile("Jane", birthDate) }
    }

    @Test
    fun `forwards a validation error unchanged`() = runTest {
        coEvery { repository.updateProfile(any(), any()) } returns
            AppResult.Error(ProfileError.ValidationError)

        val result = useCase(UpdateProfileRequest("Jane", LocalDate(2000, 1, 1)))

        assertEquals(AppResult.Error(ProfileError.ValidationError), result)
    }
}
