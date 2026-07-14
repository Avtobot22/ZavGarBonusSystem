@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.registration.presentation

import app.cash.turbine.test
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.config.AppConfig
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.domain.auth.model.RegisterRequest
import com.zavgar.system.domain.auth.usecase.RegisterUseCase
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.validation.ValidateBirthDateUseCase
import com.zavgar.system.utils.validation.ValidateNameUseCase
import com.zavgar.system.utils.validation.ValidatePhoneUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val registerUseCase = mockk<RegisterUseCase>()
    private val appConfig = mockk<AppConfig>(relaxed = true)
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = RegisterViewModel(
        ValidateNameUseCase(),
        ValidateBirthDateUseCase(),
        ValidatePhoneUseCase(),
        registerUseCase,
        appConfig,
        analyticsTracker,
    )

    private fun RegisterViewModel.fillValidForm() {
        handleIntent(RegisterIntent.EnterName("John"))
        handleIntent(RegisterIntent.CloseDatePicker(LocalDate(1990, 1, 1)))
        handleIntent(RegisterIntent.EnterPhone("1234567890"))
    }

    @Test
    fun `init seeds the privacy policy url from remote config`() {
        every { appConfig.privacyPolicyUrl } returns "https://policy"

        assertEquals("https://policy", viewModel().state.value.privacyPolicyUrl)
    }

    @Test
    fun `CloseDatePicker stores the date and its display text`() {
        val vm = viewModel()

        vm.handleIntent(RegisterIntent.CloseDatePicker(LocalDate(2000, 2, 3)))

        val state = vm.state.value
        assertEquals(LocalDate(2000, 2, 3), state.birthDate)
        assertEquals("03.02.2000", state.birthDateText)
        assertEquals(false, state.isDatePickerOpen)
    }

    @Test
    fun `Submit with an empty form sets field errors and never registers`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.handleIntent(RegisterIntent.Submit)
        advanceUntilIdle()

        val state = vm.state.value
        assertTrue(state.nameError != null)
        assertTrue(state.phoneError != null)
        coVerify(exactly = 0) { registerUseCase(any()) }
    }

    @Test
    fun `Submit with a valid form registers and navigates to confirmation`() = runTest(dispatcher) {
        coEvery {
            registerUseCase(RegisterRequest(name = "John", birthDate = LocalDate(1990, 1, 1), phone = "1234567890"))
        } returns AppResult.Success(Unit)
        val vm = viewModel().apply { fillValidForm() }

        vm.event.test {
            vm.handleIntent(RegisterIntent.Submit)
            advanceUntilIdle()
            assertEquals(RegisterEvent.NavigateToConfirm("1234567890"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(RegisterState.ScreenState.Idle, vm.state.value.screenState)
    }

    @Test
    fun `Submit shows a snackbar when registration fails`() = runTest(dispatcher) {
        coEvery { registerUseCase(any()) } returns AppResult.Error(RegisterError.UserAlreadyExists)
        val vm = viewModel().apply { fillValidForm() }

        vm.event.test {
            vm.handleIntent(RegisterIntent.Submit)
            advanceUntilIdle()
            assertTrue(awaitItem() is RegisterEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ClickLogin navigates to login`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.event.test {
            vm.handleIntent(RegisterIntent.ClickLogin)
            assertEquals(RegisterEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
