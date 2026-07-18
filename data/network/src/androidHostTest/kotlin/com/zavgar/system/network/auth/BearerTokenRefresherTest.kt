package com.zavgar.system.network.auth

import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.network.mapper.ApiException
import com.zavgar.system.network.model.LoginResponse
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BearerTokenRefresherTest {

    private val sessionDataSource = mockk<SessionDataSource>()
    private val refreshed = LoginResponse(
        accessToken = "new-access",
        accessExpiresIn = 1,
        refreshToken = "new-refresh",
        refreshExpiresIn = 2,
    )

    @Test
    fun `refresh returns and stores new tokens when old session is still current`() = runTest {
        coEvery {
            sessionDataSource.saveTokensIfRefreshTokenMatches("old-refresh", "new-access", "new-refresh")
        } returns Result.success(true)
        val refresher = BearerTokenRefresher(
            sessionDataSource = sessionDataSource,
            onRefreshRejected = {},
        )

        val result = refresher.refresh(BearerTokens("old-access", "old-refresh")) { refreshed }

        assertEquals("new-access", result?.accessToken)
        assertEquals("new-refresh", result?.refreshToken)
    }

    @Test
    fun `late successful refresh cannot overwrite a newer account session`() = runTest {
        coEvery {
            sessionDataSource.saveTokensIfRefreshTokenMatches("old-refresh", "new-access", "new-refresh")
        } returns Result.success(false)
        var logoutRequested = false
        val refresher = BearerTokenRefresher(
            sessionDataSource = sessionDataSource,
            onRefreshRejected = { logoutRequested = true },
        )

        val result = refresher.refresh(BearerTokens("old-access", "old-refresh")) { refreshed }

        assertNull(result)
        assertEquals(false, logoutRequested)
        coVerify(exactly = 1) {
            sessionDataSource.saveTokensIfRefreshTokenMatches("old-refresh", "new-access", "new-refresh")
        }
    }

    @Test
    fun `rejected refresh conditionally logs out only the token that was rejected`() = runTest {
        var rejectedToken: String? = null
        val refresher = BearerTokenRefresher(
            sessionDataSource = sessionDataSource,
            onRefreshRejected = { rejectedToken = it },
        )

        val result = refresher.refresh(BearerTokens("old-access", "old-refresh")) {
            throw ApiException(
                statusCode = 401,
                errorCode = null,
                retryAfterSeconds = null,
                errorMessage = "revoked",
            )
        }

        assertNull(result)
        assertEquals("old-refresh", rejectedToken)
        coVerify(exactly = 0) { sessionDataSource.saveTokensIfRefreshTokenMatches(any(), any(), any()) }
    }

    @Test
    fun `refresh preserves coroutine cancellation`() = runTest {
        val refresher = BearerTokenRefresher(
            sessionDataSource = sessionDataSource,
            onRefreshRejected = {},
        )

        assertFailsWith<CancellationException> {
            refresher.refresh(BearerTokens("old-access", "old-refresh")) {
                throw CancellationException("cancelled")
            }
        }
    }
}
