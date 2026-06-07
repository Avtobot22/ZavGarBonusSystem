package com.zavgar.system.domain.onboarding.usecase

import com.zavgar.system.domain.onboarding.repository.OnboardingRepository
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CompleteOnboardingUseCaseTest {

    private val repository = mockk<OnboardingRepository>()
    private val useCase = CompleteOnboardingUseCase(repository)

    @Test
    fun `delegates completion to the repository`() = runTest {
        coEvery { repository.setOnboardingCompleted() } just Runs

        useCase()

        coVerify(exactly = 1) { repository.setOnboardingCompleted() }
    }
}
