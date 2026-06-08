@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.confirmation.presentation

import app.cash.turbine.test
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.usecase.ConfirmationUseCase
import com.zavgar.system.domain.auth.usecase.ResendCodeUseCase
import com.zavgar.system.firebase.config.RemoteConfigService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.validation.ValidateCodeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
import kotlin.test.assertTrue

class ConfirmationViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val confirmationUseCase = mockk<ConfirmationUseCase>()
    private val resendCodeUseCase = mockk<ResendCodeUseCase>()
    private val remoteConfigService = mockk<RemoteConfigService>(relaxed = true)
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ConfirmationViewModel(
        ValidateCodeUseCase(),
        confirmationUseCase,
        resendCodeUseCase,
        remoteConfigService,
        analyticsTracker,
    )

    @Test
    fun `Initialize stores the phone and the registration flag`() {
        val vm = viewModel()

        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = true))

        val state = vm.state.value
        assertEquals("1234567890", state.phone)
        assertTrue(state.isRegistration)
    }

    @Test
    fun `Submit with a short code sets a code error and does not confirm`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.EnterCode("12"))

        vm.handleIntent(ConfirmationIntent.Submit)
        advanceUntilIdle()

        assertTrue(vm.state.value.codeError != null)
        coVerify(exactly = 0) { confirmationUseCase(any()) }
    }

    @Test
    fun `Submit for a login flow navigates to wallet on success`() = runTest(dispatcher) {
        coEvery { confirmationUseCase(any()) } returns AppResult.Success(Unit)
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = false))
        vm.handleIntent(ConfirmationIntent.EnterCode("1111"))

        vm.event.test {
            vm.handleIntent(ConfirmationIntent.Submit)
            advanceUntilIdle()
            assertEquals(ConfirmationEvent.NavigateToWallet, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Submit for a registration flow navigates to login on success`() = runTest(dispatcher) {
        coEvery { confirmationUseCase(any()) } returns AppResult.Success(Unit)
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = true))
        vm.handleIntent(ConfirmationIntent.EnterCode("1111"))

        vm.event.test {
            vm.handleIntent(ConfirmationIntent.Submit)
            advanceUntilIdle()
            assertEquals(ConfirmationEvent.NavigateToLogin, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Submit shows a snackbar when confirmation fails`() = runTest(dispatcher) {
        coEvery { confirmationUseCase(any()) } returns AppResult.Error(ConfirmationError.InvalidCodeError)
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = false))
        vm.handleIntent(ConfirmationIntent.EnterCode("0000"))

        vm.event.test {
            vm.handleIntent(ConfirmationIntent.Submit)
            advanceUntilIdle()
            assertTrue(awaitItem() is ConfirmationEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ClickResend is ignored while the resend timer is still running`() = runTest(dispatcher) {
        val vm = viewModel() // init starts the 60s timer

        vm.handleIntent(ConfirmationIntent.ClickResend)
        advanceUntilIdle()

        coVerify(exactly = 0) { resendCodeUseCase(any()) }
    }
}
