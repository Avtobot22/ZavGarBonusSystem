@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.settings.presentation

import app.cash.turbine.test
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.domain.theme.usecase.ObserveDarkThemeUseCase
import com.zavgar.system.domain.theme.usecase.SetDarkThemeUseCase
import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.domain.userinfo.usecase.GetUserBalanceUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.LogoutUseCase
import com.zavgar.system.utils.result.AppResult
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flowOf
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

class SettingsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val logoutUseCase = mockk<LogoutUseCase>()
    private val getUserProfileUseCase = mockk<GetUserProfileUseCase>()
    private val getUserBalanceUseCase = mockk<GetUserBalanceUseCase>()
    private val observeDarkThemeUseCase = mockk<ObserveDarkThemeUseCase>()
    private val setDarkThemeUseCase = mockk<SetDarkThemeUseCase>()
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    private val profile = UserProfile(name = "John", phone = "1234567890", birthDate = LocalDate(1990, 1, 1))

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = SettingsViewModel(
        logoutUseCase,
        getUserProfileUseCase,
        getUserBalanceUseCase,
        observeDarkThemeUseCase,
        setDarkThemeUseCase,
        analyticsTracker,
    )

    @Test
    fun `ScreenEntered loads profile, balance and theme into Content`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } returns AppResult.Success(profile)
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(900))
        every { observeDarkThemeUseCase() } returns flowOf(true)

        val vm = viewModel()
        advanceUntilIdle()
        coVerify(exactly = 0) { getUserProfileUseCase() }
        coVerify(exactly = 0) { getUserBalanceUseCase() }

        vm.handleIntent(SettingsIntent.ScreenEntered)
        advanceUntilIdle()

        val state = vm.state.value
        assertTrue(state.isDarkTheme)
        val profileState = state.profileState
        assertTrue(profileState is SettingsState.ProfileState.Content)
        assertEquals("John", profileState.name)
        assertEquals(900, profileState.balance)
    }

    @Test
    fun `ScreenEntered shows the error profile state when the profile request fails`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } returns AppResult.Error(ProfileError.ServerError)
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(0))
        every { observeDarkThemeUseCase() } returns flowOf(false)

        val vm = viewModel()
        vm.handleIntent(SettingsIntent.ScreenEntered)
        advanceUntilIdle()

        assertEquals(SettingsState.ProfileState.Error, vm.state.value.profileState)
    }

    @Test
    fun `Retry reloads the profile and balance`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } returns AppResult.Success(profile)
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(900))
        every { observeDarkThemeUseCase() } returns flowOf(false)
        val vm = viewModel()
        vm.handleIntent(SettingsIntent.ScreenEntered)
        advanceUntilIdle()

        vm.handleIntent(SettingsIntent.Retry)
        advanceUntilIdle()

        assertTrue(vm.state.value.profileState is SettingsState.ProfileState.Content)
        coVerify(exactly = 2) { getUserProfileUseCase() }
        coVerify(exactly = 2) { getUserBalanceUseCase() }
    }

    @Test
    fun `fresh ScreenEntered does not reload profile and balance`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } returns AppResult.Success(profile)
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(900))
        every { observeDarkThemeUseCase() } returns flowOf(false)
        val vm = viewModel()

        vm.handleIntent(SettingsIntent.ScreenEntered)
        advanceUntilIdle()
        vm.handleIntent(SettingsIntent.ScreenEntered)
        advanceUntilIdle()

        coVerify(exactly = 1) { getUserProfileUseCase() }
        coVerify(exactly = 1) { getUserBalanceUseCase() }
    }

    @Test
    fun `profile timeout leaves the error state instead of loading forever`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } coAnswers { awaitCancellation() }
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(900))
        every { observeDarkThemeUseCase() } returns flowOf(false)
        val vm = viewModel()

        vm.handleIntent(SettingsIntent.ScreenEntered)
        advanceUntilIdle()

        assertEquals(SettingsState.ProfileState.Error, vm.state.value.profileState)
    }

    @Test
    fun `ToggleDarkMode persists the flag and updates the state`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } returns AppResult.Success(profile)
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(0))
        every { observeDarkThemeUseCase() } returns flowOf(false)
        coEvery { setDarkThemeUseCase(any()) } just Runs
        val vm = viewModel()

        vm.handleIntent(SettingsIntent.ToggleDarkMode(isDark = true))
        advanceUntilIdle()

        assertTrue(vm.state.value.isDarkTheme)
        coVerify(exactly = 1) { setDarkThemeUseCase(true) }
    }

    @Test
    fun `Logout emits only navigation to login on success`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } returns AppResult.Success(profile)
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(0))
        every { observeDarkThemeUseCase() } returns flowOf(false)
        coEvery { logoutUseCase() } returns AppResult.Success(Unit)
        val vm = viewModel()

        vm.event.test {
            vm.handleIntent(SettingsIntent.Logout)
            advanceUntilIdle()
            assertEquals(SettingsEvent.NavigateToLogin, awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Logout shows a snackbar and resets the flag on error`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } returns AppResult.Success(profile)
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(0))
        every { observeDarkThemeUseCase() } returns flowOf(false)
        coEvery { logoutUseCase() } returns AppResult.Error(LogoutError.ServerError)
        val vm = viewModel()

        vm.event.test {
            vm.handleIntent(SettingsIntent.Logout)
            advanceUntilIdle()
            assertTrue(awaitItem() is SettingsEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(false, vm.state.value.isLoggingOut)
    }

    @Test
    fun `ToProfileDetail emits the navigation event`() = runTest(dispatcher) {
        coEvery { getUserProfileUseCase() } returns AppResult.Success(profile)
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(0))
        every { observeDarkThemeUseCase() } returns flowOf(false)
        val vm = viewModel()

        vm.event.test {
            vm.handleIntent(SettingsIntent.ToProfileDetail)
            assertEquals(SettingsEvent.NavigateToProfileDetail, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
