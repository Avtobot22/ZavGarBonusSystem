package com.zavgar.system.domain.session

import com.zavgar.system.domain.session.usecase.DeleteSessionIfCurrentUseCase
import com.zavgar.system.domain.session.usecase.DeleteSessionUseCase
import com.zavgar.system.utils.result.AppResult
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class LogoutHandlerTest {

    private val deleteSessionUseCase = mockk<DeleteSessionUseCase>()
    private val deleteSessionIfCurrentUseCase = mockk<DeleteSessionIfCurrentUseCase>()
    private val logoutNotifier = mockk<LogoutNotifier>()
    private val handler = LogoutHandler(deleteSessionUseCase, deleteSessionIfCurrentUseCase, logoutNotifier)

    @Test
    fun `conditional logout notifies when the expected session was deleted`() = runTest {
        coEvery { deleteSessionIfCurrentUseCase("refresh") } returns AppResult.Success(true)
        io.mockk.every { logoutNotifier.notifyLoggedOut() } just Runs

        handler.logoutIfCurrent("refresh")

        verify(exactly = 1) { logoutNotifier.notifyLoggedOut() }
    }

    @Test
    fun `conditional logout does not notify after account switch`() = runTest {
        coEvery { deleteSessionIfCurrentUseCase("old-refresh") } returns AppResult.Success(false)

        handler.logoutIfCurrent("old-refresh")

        verify(exactly = 0) { logoutNotifier.notifyLoggedOut() }
    }
}
