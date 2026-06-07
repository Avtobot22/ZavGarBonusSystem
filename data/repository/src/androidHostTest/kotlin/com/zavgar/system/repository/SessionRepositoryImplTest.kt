package com.zavgar.system.repository

import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.model.Session
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import com.zavgar.system.datastore.model.Session as StoredSession

class SessionRepositoryImplTest {

    private val dataSource = mockk<SessionDataSource>()
    private val repository = SessionRepositoryImpl(dataSource, TestDispatcherProvider())

    @Test
    fun `getSession maps the stored session to the domain model`() = runTest {
        coEvery { dataSource.getSession() } returns Result.success(
            StoredSession(accessToken = "access", refreshToken = "refresh", phone = "1234567890"),
        )

        val result = repository.getSession()

        assertEquals(
            AppResult.Success(Session(phone = "1234567890", accessToken = "access", refreshToken = "refresh")),
            result,
        )
    }

    @Test
    fun `getSession maps any failure to NotFound`() = runTest {
        coEvery { dataSource.getSession() } returns Result.failure(RuntimeException("missing"))

        assertEquals(AppResult.Error(SessionError.NotFound), repository.getSession())
    }

    @Test
    fun `deleteSession returns Success when the data source succeeds`() = runTest {
        coEvery { dataSource.deleteSession() } returns Result.success(Unit)

        assertEquals(AppResult.Success(Unit), repository.deleteSession())
    }

    @Test
    fun `deleteSession maps a failure to NotFound`() = runTest {
        coEvery { dataSource.deleteSession() } returns Result.failure(RuntimeException("io"))

        assertEquals(AppResult.Error(SessionError.NotFound), repository.deleteSession())
    }
}
