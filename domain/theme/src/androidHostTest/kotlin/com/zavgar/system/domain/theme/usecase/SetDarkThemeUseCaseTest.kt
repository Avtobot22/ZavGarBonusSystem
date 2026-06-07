package com.zavgar.system.domain.theme.usecase

import com.zavgar.system.domain.theme.repository.ThemeRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SetDarkThemeUseCaseTest {

    private val repository = mockk<ThemeRepository>()
    private val useCase = SetDarkThemeUseCase(repository)

    @Test
    fun `passes the enabled flag to the repository`() = runTest {
        coEvery { repository.setDarkTheme(true) } just Runs

        useCase(isDark = true)

        coVerify(exactly = 1) { repository.setDarkTheme(true) }
    }

    @Test
    fun `passes the disabled flag to the repository`() = runTest {
        coEvery { repository.setDarkTheme(false) } just Runs

        useCase(isDark = false)

        coVerify(exactly = 1) { repository.setDarkTheme(false) }
    }
}
