@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.onboarding.presentation

import app.cash.turbine.test
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.domain.onboarding.usecase.CompleteOnboardingUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OnboardingViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val completeOnboardingUseCase = mockk<CompleteOnboardingUseCase>()
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `Finish completes onboarding and navigates to login`() = runTest(dispatcher) {
        coEvery { completeOnboardingUseCase() } just Runs
        val viewModel = OnboardingViewModel(completeOnboardingUseCase, analyticsTracker)

        viewModel.event.test {
            viewModel.handleIntent(OnboardingIntent.Finish)
            advanceUntilIdle()
            assertEquals(OnboardingEvent.NavigateToLogin, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { completeOnboardingUseCase() }
    }

    @Test
    fun `Finish still navigates to login when completing onboarding throws`() = runTest(dispatcher) {
        coEvery { completeOnboardingUseCase() } throws RuntimeException("disk")
        val viewModel = OnboardingViewModel(completeOnboardingUseCase, analyticsTracker)

        viewModel.event.test {
            viewModel.handleIntent(OnboardingIntent.Finish)
            advanceUntilIdle()
            assertEquals(OnboardingEvent.NavigateToLogin, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
