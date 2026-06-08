package com.zavgar.system.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zavgar.system.coroutines.runSuspendCatching
import com.zavgar.system.datastore.exception.SessionNotFoundException
import com.zavgar.system.datastore.model.Session
import kotlinx.coroutines.flow.first

internal class SessionDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
    private val secureTokenStorage: SecureTokenStorage,
) : SessionDataSource {

    private companion object {
        val APP_PHONE = stringPreferencesKey("app_phone")
    }

    override suspend fun saveSession(session: Session): Result<Unit> =
        runSuspendCatching {
            secureTokenStorage.saveAccessToken(session.accessToken)
            secureTokenStorage.saveRefreshToken(session.refreshToken)
            dataStore.edit { settings ->
                settings[APP_PHONE] = session.phone
            }
        }

    override suspend fun saveTokens(accessToken: String, refreshToken: String): Result<Unit> =
        runSuspendCatching {
            secureTokenStorage.saveAccessToken(accessToken)
            secureTokenStorage.saveRefreshToken(refreshToken)
        }

    override suspend fun getSession(): Result<Session> =
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

    override suspend fun getAccessToken(): Result<String> =
        runSuspendCatching {
            val accessToken = secureTokenStorage.getAccessToken()
            if (!accessToken.isNullOrBlank()) {
                accessToken
            } else {
                throw SessionNotFoundException("Session data is missing in storage")
            }
        }

    override suspend fun getRefreshToken(): Result<String> =
        runSuspendCatching {
            val refreshToken = secureTokenStorage.getRefreshToken()
            if (!refreshToken.isNullOrBlank()) {
                refreshToken
            } else {
                throw SessionNotFoundException("Session data is missing in storage")
            }
        }

    override suspend fun deleteSession(): Result<Unit> =
        runSuspendCatching {
            secureTokenStorage.clear()
            dataStore.edit { settings ->
                settings.remove(APP_PHONE)
            }
        }
}
