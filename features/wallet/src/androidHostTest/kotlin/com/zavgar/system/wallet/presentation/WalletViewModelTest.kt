@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.wallet.presentation

import com.zavgar.system.domain.session.LogoutHandler
import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.model.Session
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.error.MonthlyAccrualsError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.usecase.GetCachedBalanceUseCase
import com.zavgar.system.domain.userinfo.usecase.GetMonthlyAccrualsUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserBalanceUseCase
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.utils.result.AppResult
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
import kotlin.test.assertTrue

class WalletViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getUserBalanceUseCase = mockk<GetUserBalanceUseCase>()
    private val getCachedBalanceUseCase = mockk<GetCachedBalanceUseCase>()
    private val getMonthlyAccrualsUseCase = mockk<GetMonthlyAccrualsUseCase>()
    private val getSessionUseCase = mockk<GetSessionUseCase>()
    private val logoutHandler = mockk<LogoutHandler>(relaxed = true)
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = WalletViewModel(
        getUserBalanceUseCase,
        getCachedBalanceUseCase,
        getMonthlyAccrualsUseCase,
        getSessionUseCase,
        logoutHandler,
        analyticsTracker,
    )

    @Test
    fun `init loads the balance and monthly accruals into Content`() = runTest(dispatcher) {
        coEvery { getSessionUseCase() } returns AppResult.Success(Session("1234567890", "a", "r"))
        coEvery { getCachedBalanceUseCase() } returns null
        coEvery { getUserBalanceUseCase() } returns AppResult.Success(Balance(500))
        coEvery { getMonthlyAccrualsUseCase() } returns AppResult.Success(30)

        val vm = viewModel()
        advanceUntilIdle()

        val state = vm.state.value
        val screenState = state.screenState
        assertTrue(screenState is WalletState.ScreenState.Content)
        assertEquals(500, screenState.balance)
        assertEquals(30, state.monthlyEarned)
        assertEquals("1234567890", state.phone)
    }

    @Test
    fun `init logs out when there is no session`() = runTest(dispatcher) {
        coEvery { getSessionUseCase() } returns AppResult.Error(SessionError.NotFound)
        coEvery { logoutHandler.logout() } just Runs

        viewModel()
        advanceUntilIdle()

        coVerify(exactly = 1) { logoutHandler.logout() }
    }

    @Test
    fun `init falls back to Offline when the balance request fails without cache`() = runTest(dispatcher) {
        coEvery { getSessionUseCase() } returns AppResult.Success(Session("1234567890", "a", "r"))
        coEvery { getCachedBalanceUseCase() } returns null
        coEvery { getUserBalanceUseCase() } returns AppResult.Error(GetBalanceError.NetworkError)
        coEvery { getMonthlyAccrualsUseCase() } returns AppResult.Error(MonthlyAccrualsError.NetworkError)

        val vm = viewModel()
        advanceUntilIdle()

        assertEquals(WalletState.ScreenState.Offline, vm.state.value.screenState)
    }
}
