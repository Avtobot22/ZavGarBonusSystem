package com.zavgar.system.domain.theme.usecase

import app.cash.turbine.test
import com.zavgar.system.domain.theme.repository.ThemeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveDarkThemeUseCaseTest {

    private val repository = mockk<ThemeRepository>()
    private val useCase = ObserveDarkThemeUseCase(repository)

    @Test
    fun `emits the dark-theme values exposed by the repository flow`() = runTest {
        every { repository.isDarkTheme } returns flowOf(true, false)

        useCase().test {
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            awaitComplete()
        }
    }
}
