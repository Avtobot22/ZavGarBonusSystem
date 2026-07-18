package com.zavgar.system.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zavgar.system.coroutines.runSuspendCatching
import com.zavgar.system.datastore.exception.SessionNotFoundException
import com.zavgar.system.datastore.model.Session
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class SessionDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
    private val secureTokenStorage: SecureTokenStorage,
) : SessionDataSource {

    private val sessionMutex = Mutex()

    private companion object {
        val APP_PHONE = stringPreferencesKey("app_phone")
    }

    override suspend fun saveSession(session: Session): Result<Unit> = sessionMutex.withLock {
        runSuspendCatching {
            secureTokenStorage.saveAccessToken(session.accessToken)
            secureTokenStorage.saveRefreshToken(session.refreshToken)
            dataStore.edit { settings ->
                settings[APP_PHONE] = session.phone
            }
            Unit
        }
    }

    override suspend fun saveTokensIfRefreshTokenMatches(
        expectedRefreshToken: String,
        accessToken: String,
        refreshToken: String,
    ): Result<Boolean> = sessionMutex.withLock {
        runSuspendCatching {
            if (secureTokenStorage.getRefreshToken() != expectedRefreshToken) {
                return@runSuspendCatching false
            }
            secureTokenStorage.saveAccessToken(accessToken)
            secureTokenStorage.saveRefreshToken(refreshToken)
            true
        }
    }

    override suspend fun getSession(): Result<Session> = sessionMutex.withLock {
        runSuspendCatching {
            val accessToken = secureTokenStorage.getAccessToken()
            val refreshToken = secureTokenStorage.getRefreshToken()
            val phone = dataStore.data.first()[APP_PHONE]

            if (!accessToken.isNullOrBlank() && !phone.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                Session(accessToken = accessToken, phone = phone, refreshToken = refreshToken)
            } else {
                throw SessionNotFoundException("Session data is missing in storage")
            }
        }
    }

    override suspend fun getAccessToken(): Result<String> = sessionMutex.withLock {
        runSuspendCatching {
            val accessToken = secureTokenStorage.getAccessToken()
            if (!accessToken.isNullOrBlank()) {
                accessToken
            } else {
                throw SessionNotFoundException("Session data is missing in storage")
            }
        }
    }

    override suspend fun getRefreshToken(): Result<String> = sessionMutex.withLock {
        runSuspendCatching {
            val refreshToken = secureTokenStorage.getRefreshToken()
            if (!refreshToken.isNullOrBlank()) {
                refreshToken
            } else {
                throw SessionNotFoundException("Session data is missing in storage")
            }
        }
    }

    override suspend fun deleteSession(): Result<Unit> = sessionMutex.withLock {
        runSuspendCatching { deleteSessionData() }
    }

    override suspend fun deleteSessionIfRefreshTokenMatches(expectedRefreshToken: String): Result<Boolean> =
        sessionMutex.withLock {
            runSuspendCatching {
                if (secureTokenStorage.getRefreshToken() != expectedRefreshToken) {
                    return@runSuspendCatching false
                }
                deleteSessionData()
                true
            }
        }

    private suspend fun deleteSessionData() {
        secureTokenStorage.clear()
        dataStore.edit { settings ->
            settings.remove(APP_PHONE)
        }
    }
}
