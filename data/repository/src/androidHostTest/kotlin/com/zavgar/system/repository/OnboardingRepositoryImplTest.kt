package com.zavgar.system.repository

import app.cash.turbine.test
import com.zavgar.system.datastore.datasource.OnboardingDataSource
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

class OnboardingRepositoryImplTest {

    private val dataSource = mockk<OnboardingDataSource>()

    @Test
    fun `isOnboardingCompleted exposes the data source flow`() = runTest {
        every { dataSource.isOnboardingCompleted } returns flowOf(true)
        val repository = OnboardingRepositoryImpl(dataSource)

        repository.isOnboardingCompleted.test {
            assertEquals(true, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setOnboardingCompleted delegates to the data source`() = runTest {
        every { dataSource.isOnboardingCompleted } returns flowOf(false)
        coEvery { dataSource.setOnboardingCompleted() } just Runs
        val repository = OnboardingRepositoryImpl(dataSource)

        repository.setOnboardingCompleted()

        coVerify(exactly = 1) { dataSource.setOnboardingCompleted() }
    }
}
