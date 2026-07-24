@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.confirmation.presentation

import app.cash.turbine.test
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.config.AppConfig
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.domain.auth.usecase.ConfirmationUseCase
import com.zavgar.system.domain.auth.usecase.ResendCodeUseCase
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.validation.ValidateCodeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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
    private val appConfig = mockk<AppConfig>(relaxed = true)
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ConfirmationViewModel(
        ValidateCodeUseCase(),
        confirmationUseCase,
        resendCodeUseCase,
        appConfig,
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
    fun `Submit for a registration flow navigates to wallet on success`() = runTest(dispatcher) {
        coEvery { confirmationUseCase(any()) } returns AppResult.Success(Unit)
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = true))
        vm.handleIntent(ConfirmationIntent.EnterCode("1111"))

        vm.event.test {
            vm.handleIntent(ConfirmationIntent.Submit)
            advanceUntilIdle()
            assertEquals(ConfirmationEvent.NavigateToWallet, awaitItem())
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
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = false))

        vm.handleIntent(ConfirmationIntent.ClickResend)
        advanceUntilIdle()

        coVerify(exactly = 0) { resendCodeUseCase(any()) }
    }

    @Test
    fun `resend restarts the timer on success`() = runTest(dispatcher) {
        coEvery { resendCodeUseCase(any()) } returns AppResult.Success(Unit)
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = false))
        advanceUntilIdle() // let the entry timer run down to 0

        vm.handleIntent(ConfirmationIntent.ClickResend)
        // runCurrent выполняет тело resend (и startTimer) до первого delay таймера,
        // поэтому видим перезапущенный таймер на полной длительности, не дожидаясь обратного отсчёта.
        runCurrent()
        assertEquals(TIMER_DURATION_SECONDS, vm.state.value.timerSeconds)

        advanceUntilIdle()
        coVerify { resendCodeUseCase(any()) }
    }

    @Test
    fun `resend does not start the timer on a generic error`() = runTest(dispatcher) {
        coEvery { resendCodeUseCase(any()) } returns AppResult.Error(ResendConfirmationError.ServerError)
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = false))
        advanceUntilIdle() // entry timer down to 0

        vm.handleIntent(ConfirmationIntent.ClickResend)
        advanceUntilIdle()

        assertEquals(0, vm.state.value.timerSeconds)
    }

    @Test
    fun `resend starts the timer with retryAfterSeconds on TooManyRequest`() = runTest(dispatcher) {
        coEvery { resendCodeUseCase(any()) } returns
            AppResult.Error(ResendConfirmationError.TooManyRequestError(retryAfterSeconds = 30L))
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = false))
        advanceUntilIdle() // entry timer down to 0

        vm.handleIntent(ConfirmationIntent.ClickResend)
        runCurrent()

        assertEquals(RETRY_AFTER_SECONDS, vm.state.value.timerSeconds)
        advanceUntilIdle()
    }

    @Test
    fun `repeated Initialize does not restart the timer`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = false))
        runCurrent()
        advanceTimeBy(1000)
        runCurrent()
        assertEquals(TIMER_DURATION_SECONDS - 1, vm.state.value.timerSeconds)

        vm.handleIntent(ConfirmationIntent.Initialize(phone = "1234567890", isRegistration = false))

        assertEquals(TIMER_DURATION_SECONDS - 1, vm.state.value.timerSeconds)
    }

    private companion object {
        const val TIMER_DURATION_SECONDS = 60
        const val RETRY_AFTER_SECONDS = 30
    }
}
