package com.zavgar.system.datastore.datasource

import com.zavgar.system.datastore.model.Session
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SessionDataSourceImplTest {

    private val secureTokenStorage = FakeSecureTokenStorage()
    private val dataSource = SessionDataSourceImpl(InMemoryPreferencesDataStore(), secureTokenStorage)

    @Test
    fun `token refresh uses compare and set and cannot overwrite a newer session`() = runTest {
        dataSource.saveSession(Session("access-b", "refresh-b", "account-b")).getOrThrow()

        val saved = dataSource.saveTokensIfRefreshTokenMatches(
            expectedRefreshToken = "refresh-a",
            accessToken = "new-access-a",
            refreshToken = "new-refresh-a",
        ).getOrThrow()

        assertFalse(saved)
        assertEquals(Session("access-b", "refresh-b", "account-b"), dataSource.getSession().getOrThrow())
    }

    @Test
    fun `token refresh updates tokens when expected refresh token is current`() = runTest {
        dataSource.saveSession(Session("access-a", "refresh-a", "account-a")).getOrThrow()

        val saved = dataSource.saveTokensIfRefreshTokenMatches(
            expectedRefreshToken = "refresh-a",
            accessToken = "new-access-a",
            refreshToken = "new-refresh-a",
        ).getOrThrow()

        assertTrue(saved)
        assertEquals(Session("new-access-a", "new-refresh-a", "account-a"), dataSource.getSession().getOrThrow())
    }

    @Test
    fun `conditional deletion preserves a different account session`() = runTest {
        dataSource.saveSession(Session("access-b", "refresh-b", "account-b")).getOrThrow()

        val deleted = dataSource.deleteSessionIfRefreshTokenMatches("refresh-a").getOrThrow()

        assertFalse(deleted)
        assertEquals(Session("access-b", "refresh-b", "account-b"), dataSource.getSession().getOrThrow())
    }
}
