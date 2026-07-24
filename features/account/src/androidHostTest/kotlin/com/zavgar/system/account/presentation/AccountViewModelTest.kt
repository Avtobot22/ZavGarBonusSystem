@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.zavgar.system.account.presentation

import app.cash.turbine.test
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.domain.userinfo.usecase.DeleteUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.UpdateUserProfileUseCase
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.validation.ValidateBirthDateUseCase
import com.zavgar.system.utils.validation.ValidateNameUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
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

class AccountViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val getProfileUseCase = mockk<GetUserProfileUseCase>()
    private val updateProfileUseCase = mockk<UpdateUserProfileUseCase>()
    private val deleteProfileUseCase = mockk<DeleteUserProfileUseCase>()
    private val analyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    private val profile = UserProfile(name = "John", phone = "1234567890", birthDate = LocalDate(1990, 1, 1))

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = AccountViewModel(
        getProfileUseCase,
        updateProfileUseCase,
        deleteProfileUseCase,
        ValidateNameUseCase(),
        ValidateBirthDateUseCase(),
        analyticsTracker,
    )

    @Test
    fun `ScreenEntered populates the form from the loaded profile`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)

        val vm = viewModel()
        advanceUntilIdle()
        coVerify(exactly = 0) { getProfileUseCase() }

        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        val state = vm.state.value
        assertEquals(AccountState.ScreenState.Content, state.screenState)
        assertEquals("John", state.name)
        assertEquals("1234567890", state.phone)
        assertEquals(LocalDate(1990, 1, 1), state.birthDate)
        assertEquals("01.01.1990", state.birthDateText)
    }

    @Test
    fun `ScreenEntered shows the error state when loading the profile fails`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Error(ProfileError.ServerError)

        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        assertEquals(AccountState.ScreenState.Error, vm.state.value.screenState)
    }

    @Test
    fun `fresh ScreenEntered is skipped while Retry forces reload`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        val vm = viewModel()

        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        coVerify(exactly = 1) { getProfileUseCase() }

        vm.handleIntent(AccountIntent.Retry)
        advanceUntilIdle()

        coVerify(exactly = 2) { getProfileUseCase() }
    }

    @Test
    fun `profile timeout leaves the error state instead of loading forever`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } coAnswers { awaitCancellation() }
        val vm = viewModel()

        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        assertEquals(AccountState.ScreenState.Error, vm.state.value.screenState)
    }

    @Test
    fun `Submit with a blank name sets an error and does not update`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()
        vm.handleIntent(AccountIntent.EnterName(""))

        vm.handleIntent(AccountIntent.Submit)
        advanceUntilIdle()

        assertTrue(vm.state.value.nameError != null)
        coVerify(exactly = 0) { updateProfileUseCase(any()) }
    }

    @Test
    fun `Submit with a valid form updates the profile and shows a success snackbar`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        coEvery { updateProfileUseCase(any()) } returns AppResult.Success(Unit)
        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        vm.event.test {
            vm.handleIntent(AccountIntent.Submit)
            advanceUntilIdle()
            assertTrue(awaitItem() is AccountEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { updateProfileUseCase(any()) }
    }

    @Test
    fun `Submit enters Submitting while updating and returns to Content afterwards`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        // Удерживаем обновление «в полёте» через deferred, чтобы наблюдать Submitting.
        val gate = CompletableDeferred<AppResult<Unit, ProfileError>>()
        coEvery { updateProfileUseCase(any()) } coAnswers { gate.await() }
        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        vm.handleIntent(AccountIntent.Submit)
        advanceUntilIdle()
        assertEquals(AccountState.ScreenState.Submitting, vm.state.value.screenState)
        assertTrue(vm.state.value.isSubmitting)

        gate.complete(AppResult.Success(Unit))
        advanceUntilIdle()
        assertEquals(AccountState.ScreenState.Content, vm.state.value.screenState)
    }

    @Test
    fun `Submit is ignored while a submit is already in flight`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        val gate = CompletableDeferred<AppResult<Unit, ProfileError>>()
        coEvery { updateProfileUseCase(any()) } coAnswers { gate.await() }
        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        vm.handleIntent(AccountIntent.Submit)
        advanceUntilIdle() // VM теперь в Submitting (update удерживается deferred)
        vm.handleIntent(AccountIntent.Submit) // повтор должен игнорироваться
        advanceUntilIdle()

        gate.complete(AppResult.Success(Unit))
        advanceUntilIdle()
        coVerify(exactly = 1) { updateProfileUseCase(any()) }
    }

    @Test
    fun `ClickDelete opens the confirmation dialog`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        vm.handleIntent(AccountIntent.ClickDelete)

        assertTrue(vm.state.value.confirmDeleteDialog)
    }

    @Test
    fun `ConfirmDeleteAccount emits only navigation to login on success`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        coEvery { deleteProfileUseCase() } returns AppResult.Success(Unit)
        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        vm.event.test {
            vm.handleIntent(AccountIntent.ConfirmDeleteAccount)
            advanceUntilIdle()
            assertEquals(AccountEvent.NavigateToLogin, awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ConfirmDeleteAccount shows a snackbar on failure`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        coEvery { deleteProfileUseCase() } returns AppResult.Error(DeleteError.ServerError)
        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        vm.event.test {
            vm.handleIntent(AccountIntent.ConfirmDeleteAccount)
            advanceUntilIdle()
            assertTrue(awaitItem() is AccountEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ClickBack emits a navigate-back event`() = runTest(dispatcher) {
        coEvery { getProfileUseCase() } returns AppResult.Success(profile)
        val vm = viewModel()
        vm.handleIntent(AccountIntent.ScreenEntered)
        advanceUntilIdle()

        vm.event.test {
            vm.handleIntent(AccountIntent.ClickBack)
            assertEquals(AccountEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
