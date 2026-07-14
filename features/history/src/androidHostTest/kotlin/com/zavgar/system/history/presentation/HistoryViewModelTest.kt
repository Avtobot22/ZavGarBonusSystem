@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.history.presentation

import app.cash.turbine.test
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.domain.operations.error.OperationsError
import com.zavgar.system.domain.operations.model.OperationType
import com.zavgar.system.domain.operations.model.PointsType
import com.zavgar.system.domain.operations.model.Transaction
import com.zavgar.system.domain.operations.model.TransactionsPageResponse
import com.zavgar.system.domain.operations.usecase.GetOperationsUseCase
import com.zavgar.system.history.model.DatePickerType
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

class HistoryViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getOperationsUseCase = mockk<GetOperationsUseCase>()
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    // Fixed clock so the default period (first-of-month .. today) is deterministic.
    private val fixedClock = object : Clock {
        override fun now(): Instant = Instant.parse("2026-05-15T10:00:00Z")
    }

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = HistoryViewModel(getOperationsUseCase, analyticsTracker, fixedClock)

    private fun page() = TransactionsPageResponse(
        transactions = listOf(
            Transaction(
                id = 1,
                operationType = OperationType.CREDITING,
                date = LocalDateTime(2026, 5, 10, 12, 0),
                store = "Store",
                amount = 100,
                pointsType = PointsType.BONUS,
            ),
        ),
        newCursor = null,
        hasMore = false,
    )

    @Test
    fun `ScreenEntered loads the first page into Content`() = runTest(dispatcher) {
        coEvery { getOperationsUseCase(any()) } returns AppResult.Success(page())

        val vm = viewModel()
        advanceUntilIdle()
        coVerify(exactly = 0) { getOperationsUseCase(any()) }

        vm.handleIntent(HistoryIntent.ScreenEntered)
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(HistoryState.ScreenState.Content, state.screenState)
        assertTrue(state.history.transactions.isNotEmpty())
    }

    @Test
    fun `ScreenEntered shows the error state when the first page fails and nothing is loaded`() = runTest(dispatcher) {
        coEvery { getOperationsUseCase(any()) } returns AppResult.Error(OperationsError.ServerError)

        val vm = viewModel()
        vm.handleIntent(HistoryIntent.ScreenEntered)
        advanceUntilIdle()

        assertEquals(HistoryState.ScreenState.Error, vm.state.value.screenState)
    }

    @Test
    fun `fresh ScreenEntered is skipped while Refresh forces reload`() = runTest(dispatcher) {
        coEvery { getOperationsUseCase(any()) } returns AppResult.Success(page())
        val vm = viewModel()

        vm.handleIntent(HistoryIntent.ScreenEntered)
        advanceUntilIdle()
        vm.handleIntent(HistoryIntent.ScreenEntered)
        advanceUntilIdle()

        coVerify(exactly = 1) { getOperationsUseCase(any()) }

        vm.handleIntent(HistoryIntent.Refresh)
        advanceUntilIdle()

        coVerify(exactly = 2) { getOperationsUseCase(any()) }
    }

    @Test
    fun `first page timeout leaves the error state instead of loading forever`() = runTest(dispatcher) {
        coEvery { getOperationsUseCase(any()) } coAnswers { awaitCancellation() }
        val vm = viewModel()

        vm.handleIntent(HistoryIntent.ScreenEntered)
        advanceUntilIdle()

        assertEquals(HistoryState.ScreenState.Error, vm.state.value.screenState)
    }

    @Test
    fun `the initial period spans from the first of the month to the clock's today`() = runTest(dispatcher) {
        coEvery { getOperationsUseCase(any()) } returns AppResult.Success(page())

        val vm = viewModel()

        val state = vm.state.value
        assertEquals(LocalDate(2026, 5, 1), state.periodStart)
        assertEquals(LocalDate(2026, 5, 15), state.periodEnd)
    }

    @Test
    fun `closing the start picker after the end date warns and keeps the picker closed`() = runTest(dispatcher) {
        coEvery { getOperationsUseCase(any()) } returns AppResult.Success(page())
        val vm = viewModel()

        vm.event.test {
            // Start (2026-05-20) is after end (2026-05-15) -> invalid range.
            vm.handleIntent(HistoryIntent.CloseDatePicker(DatePickerType.START, LocalDate(2026, 5, 20)))
            advanceUntilIdle()
            assertTrue(awaitItem() is HistoryEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(null, vm.state.value.datePickerOpen)
        // Period start is unchanged because the range was rejected.
        assertEquals(LocalDate(2026, 5, 1), vm.state.value.periodStart)
    }
}
