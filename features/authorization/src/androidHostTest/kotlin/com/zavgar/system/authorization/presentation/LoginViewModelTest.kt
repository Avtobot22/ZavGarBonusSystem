@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.authorization.presentation

import app.cash.turbine.test
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.domain.auth.model.LoginRequest
import com.zavgar.system.domain.auth.usecase.LoginUseCase
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.validation.ValidatePhoneUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val loginUseCase = mockk<LoginUseCase>()
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)
    private val viewModel by lazy {
        LoginViewModel(ValidatePhoneUseCase(), loginUseCase, analyticsTracker)
    }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `EnterPhone sanitizes the input and marks a full number as valid`() {
        viewModel.handleIntent(LoginIntent.EnterPhone("+1 (234) 567-890"))

        val state = viewModel.state.value
        assertEquals("1234567890", state.phone)
        assertTrue(state.isPhoneValid)
        assertEquals(null, state.phoneError)
    }

    @Test
    fun `EnterPhone marks an incomplete number as invalid`() {
        viewModel.handleIntent(LoginIntent.EnterPhone("123"))

        assertFalse(viewModel.state.value.isPhoneValid)
    }

    @Test
    fun `Submit with an invalid phone sets an error and never calls the use case`() = runTest(dispatcher) {
        viewModel.handleIntent(LoginIntent.EnterPhone("123"))

        viewModel.handleIntent(LoginIntent.Submit)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.phoneError != null)
        coVerify(exactly = 0) { loginUseCase(any()) }
    }

    @Test
    fun `Submit with a valid phone requests OTP and navigates to confirmation`() = runTest(dispatcher) {
        coEvery { loginUseCase(LoginRequest("1234567890")) } returns AppResult.Success(Unit)
        viewModel.handleIntent(LoginIntent.EnterPhone("1234567890"))

        viewModel.event.test {
            viewModel.handleIntent(LoginIntent.Submit)
            advanceUntilIdle()
            assertEquals(LoginEvent.NavigateToConfirmation("1234567890"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(LoginState.ScreenState.Idle, viewModel.state.value.screenState)
        coVerify(exactly = 1) { loginUseCase(LoginRequest("1234567890")) }
    }

    @Test
    fun `Submit shows a snackbar when the use case returns an error`() = runTest(dispatcher) {
        coEvery { loginUseCase(any()) } returns AppResult.Error(AuthError.UserNotFound)
        viewModel.handleIntent(LoginIntent.EnterPhone("1234567890"))

        viewModel.event.test {
            viewModel.handleIntent(LoginIntent.Submit)
            advanceUntilIdle()
            assertTrue(awaitItem() is LoginEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(LoginState.ScreenState.Idle, viewModel.state.value.screenState)
    }

    @Test
    fun `ClickRegister emits a navigate-to-register event`() = runTest(dispatcher) {
        viewModel.event.test {
            viewModel.handleIntent(LoginIntent.ClickRegister)
            assertEquals(LoginEvent.NavigateToRegister, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
