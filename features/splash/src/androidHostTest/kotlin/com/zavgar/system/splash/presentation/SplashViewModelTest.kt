@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.splash.presentation

import app.cash.turbine.test
import com.zavgar.system.domain.onboarding.usecase.ObserveOnboardingCompletedUseCase
import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.model.Session
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
import com.zavgar.system.firebase.config.RemoteConfigService
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SplashViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val remoteConfigService = mockk<RemoteConfigService>(relaxed = true)
    private val getSessionUseCase = mockk<GetSessionUseCase>()
    private val observeOnboardingCompletedUseCase = mockk<ObserveOnboardingCompletedUseCase>()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = SplashViewModel(
        remoteConfigService,
        getSessionUseCase,
        observeOnboardingCompletedUseCase,
    )

    @Test
    fun `navigates to wallet when a session exists`() = runTest(dispatcher) {
        coEvery { getSessionUseCase() } returns
            AppResult.Success(Session("1234567890", "access", "refresh"))
        every { observeOnboardingCompletedUseCase() } returns flowOf(true)

        viewModel().event.test {
            advanceUntilIdle()
            assertEquals(SplashEvent.NavigateToWallet, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigates to login when there is no session but onboarding is completed`() = runTest(dispatcher) {
        coEvery { getSessionUseCase() } returns AppResult.Error(SessionError.NotFound)
        every { observeOnboardingCompletedUseCase() } returns flowOf(true)

        viewModel().event.test {
            advanceUntilIdle()
            assertEquals(SplashEvent.NavigateToLogin, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigates to onboarding when there is no session and onboarding is not completed`() = runTest(dispatcher) {
        coEvery { getSessionUseCase() } returns AppResult.Error(SessionError.NotFound)
        every { observeOnboardingCompletedUseCase() } returns flowOf(false)

        viewModel().event.test {
            advanceUntilIdle()
            assertEquals(SplashEvent.NavigateToOnboarding, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `falls back to login when initialization throws`() = runTest(dispatcher) {
        coEvery { remoteConfigService.activate() } throws RuntimeException("config down")

        viewModel().event.test {
            advanceUntilIdle()
            assertEquals(SplashEvent.NavigateToLogin, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
