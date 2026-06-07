package com.zavgar.system.repository

import app.cash.turbine.test
import com.zavgar.system.datastore.datasource.ThemeDataSource
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeRepositoryImplTest {

    private val dataSource = mockk<ThemeDataSource>()

    @Test
    fun `isDarkTheme exposes the data source flow`() = runTest {
        every { dataSource.isDarkTheme } returns flowOf(false, true)
        val repository = ThemeRepositoryImpl(dataSource)

        repository.isDarkTheme.test {
            assertEquals(false, awaitItem())
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setDarkTheme delegates to the data source`() = runTest {
        every { dataSource.isDarkTheme } returns flowOf(false)
        coEvery { dataSource.setDarkTheme(any()) } just Runs
        val repository = ThemeRepositoryImpl(dataSource)

        repository.setDarkTheme(true)

        coVerify(exactly = 1) { dataSource.setDarkTheme(true) }
    }
}
