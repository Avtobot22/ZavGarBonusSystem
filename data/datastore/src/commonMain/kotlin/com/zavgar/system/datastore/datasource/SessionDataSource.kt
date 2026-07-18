package com.zavgar.system.datastore.datasource

import com.zavgar.system.datastore.model.Session

interface SessionDataSource {

    suspend fun saveSession(session: Session): Result<Unit>

    suspend fun getSession(): Result<Session>

    suspend fun saveTokensIfRefreshTokenMatches(
        expectedRefreshToken: String,
        accessToken: String,
        refreshToken: String,
    ): Result<Boolean>

    suspend fun getRefreshToken(): Result<String>

    suspend fun getAccessToken(): Result<String>

    suspend fun deleteSession(): Result<Unit>

    suspend fun deleteSessionIfRefreshTokenMatches(expectedRefreshToken: String): Result<Boolean>
}
