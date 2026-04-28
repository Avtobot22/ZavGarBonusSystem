package com.zavgar.system.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.zavgar.system.coroutines.runSuspendCatching
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zavgar.system.datastore.exception.SessionNotFoundException
import com.zavgar.system.datastore.mapper.toDataStore
import com.zavgar.system.datastore.mapper.toRepo
import com.zavgar.system.repository.datasource.SessionDataSource
import kotlinx.coroutines.flow.first
import com.zavgar.system.datastore.model.Session as DataStoreSession
import com.zavgar.system.repository.model.response.Session as RepoSession

internal class SessionDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : SessionDataSource {

    private companion object {
        val APP_ACCESS_TOKEN = stringPreferencesKey("app_access_token")

        val APP_REFRESH_TOKEN = stringPreferencesKey("app_refresh_token")

        val APP_PHONE = stringPreferencesKey("app_phone")
    }

    override suspend fun saveSession(session: RepoSession): Result<Unit> {
        return runSuspendCatching {
            val dataStoreSession = session.toDataStore()

            dataStore.edit { settings ->
                settings[APP_ACCESS_TOKEN] = dataStoreSession.accessToken
                settings[APP_REFRESH_TOKEN] = dataStoreSession.refreshToken
                settings[APP_PHONE] = dataStoreSession.phone
            }
        }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String): Result<Unit> {
        return runSuspendCatching {
            dataStore.edit { settings ->
                settings[APP_ACCESS_TOKEN] = accessToken
                settings[APP_REFRESH_TOKEN] = refreshToken
            }
        }
    }

    override suspend fun getSession(): Result<RepoSession> {
        return runSuspendCatching {
            val preferences = dataStore.data.first()

            val accessToken = preferences[APP_ACCESS_TOKEN]
            val refreshToken = preferences[APP_REFRESH_TOKEN]
            val phone = preferences[APP_PHONE]

            if (!accessToken.isNullOrBlank() && !phone.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                val session = DataStoreSession(accessToken = accessToken, phone = phone, refreshToken = refreshToken)
                session.toRepo()
            } else {
                throw SessionNotFoundException("Session data is missing in storage")
            }
        }
    }

    override suspend fun getAccessToken(): Result<String> {
        return runSuspendCatching {
            val preferences = dataStore.data.first()

            val accessToken = preferences[APP_ACCESS_TOKEN]

            if (!accessToken.isNullOrBlank()) {
                accessToken
            } else {
                throw SessionNotFoundException("Session data is missing in storage")
            }
        }
    }

    override suspend fun getRefreshToken(): Result<String> {
        return runSuspendCatching {
            val preferences = dataStore.data.first()

            val refreshToken = preferences[APP_REFRESH_TOKEN]

            if (!refreshToken.isNullOrBlank()) {
                refreshToken
            } else {
                throw SessionNotFoundException("Session data is missing in storage")
            }
        }
    }

    override suspend fun deleteSession(): Result<Unit> {
        return runSuspendCatching {
            dataStore.edit { settings ->
                settings.clear()
            }
        }
    }
}