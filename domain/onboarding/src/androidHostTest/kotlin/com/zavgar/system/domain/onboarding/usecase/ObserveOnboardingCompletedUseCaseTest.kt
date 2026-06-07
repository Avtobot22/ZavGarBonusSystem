package com.zavgar.system.domain.onboarding.usecase

import app.cash.turbine.test
import com.zavgar.system.domain.onboarding.repository.OnboardingRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObserveOnboardingCompletedUseCaseTest {

    private val repository = mockk<OnboardingRepository>()
    private val useCase = ObserveOnboardingCompletedUseCase(repository)

    @Test
    fun `emits the values exposed by the repository flow`() = runTest {
        every { repository.isOnboardingCompleted } returns flowOf(false, true)

        useCase().test {
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `returns the repository flow instance directly`() {
        val flow = flowOf(true)
        every { repository.isOnboardingCompleted } returns flow

        assertTrue(useCase() === flow)
    }
}
