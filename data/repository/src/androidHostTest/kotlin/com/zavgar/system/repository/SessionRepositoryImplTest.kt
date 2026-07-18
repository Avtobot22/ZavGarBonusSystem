package com.zavgar.system.repository

import com.zavgar.system.datastore.datasource.BalanceCacheDataSource
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.model.Session
import com.zavgar.system.network.auth.AuthTokenCache
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import com.zavgar.system.datastore.model.Session as StoredSession

class SessionRepositoryImplTest {

    private val dataSource = mockk<SessionDataSource>()
    private val balanceCacheDataSource = mockk<BalanceCacheDataSource>(relaxed = true)
    private val authTokenCache = mockk<AuthTokenCache>(relaxed = true)
    private val repository = SessionRepositoryImpl(
        dataSource,
        balanceCacheDataSource,
        authTokenCache,
        TestDispatcherProvider(),
    )

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
        coVerify(exactly = 1) { balanceCacheDataSource.clearBalance() }
        verify(exactly = 1) { authTokenCache.clear() }
    }

    @Test
    fun `deleteSession maps a failure to NotFound`() = runTest {
        coEvery { dataSource.deleteSession() } returns Result.failure(RuntimeException("io"))

        assertEquals(AppResult.Error(SessionError.NotFound), repository.deleteSession())
        coVerify(exactly = 0) { balanceCacheDataSource.clearBalance() }
        verify(exactly = 1) { authTokenCache.clear() }
    }

    @Test
    fun `conditional deletion clears user data when refresh token matches`() = runTest {
        coEvery { dataSource.deleteSessionIfRefreshTokenMatches("refresh") } returns Result.success(true)

        assertEquals(AppResult.Success(true), repository.deleteSessionIfRefreshTokenMatches("refresh"))
        coVerify(exactly = 1) { balanceCacheDataSource.clearBalance() }
        verify(exactly = 1) { authTokenCache.clear() }
    }

    @Test
    fun `conditional deletion preserves new account data when refresh token changed`() = runTest {
        coEvery { dataSource.deleteSessionIfRefreshTokenMatches("old-refresh") } returns Result.success(false)

        assertEquals(AppResult.Success(false), repository.deleteSessionIfRefreshTokenMatches("old-refresh"))
        coVerify(exactly = 0) { balanceCacheDataSource.clearBalance() }
        verify(exactly = 0) { authTokenCache.clear() }
    }

    @Test
    fun `logout succeeds when non-critical balance cleanup fails`() = runTest {
        coEvery { dataSource.deleteSession() } returns Result.success(Unit)
        coEvery { balanceCacheDataSource.clearBalance() } throws RuntimeException("cache")

        assertEquals(AppResult.Success(Unit), repository.deleteSession())
        verify(exactly = 1) { authTokenCache.clear() }
    }
}
