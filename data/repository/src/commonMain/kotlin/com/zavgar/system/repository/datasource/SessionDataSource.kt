package com.zavgar.system.repository.datasource

import com.zavgar.system.repository.model.response.Session

interface SessionDataSource {

    suspend fun saveSession(session: Session): Result<Unit>

    suspend fun getSession(): Result<Session>

    suspend fun saveTokens(accessToken: String, refreshToken: String): Result<Unit>


    suspend fun getRefreshToken(): Result<String>

    suspend fun getAccessToken(): Result<String>

    suspend fun deleteSession(): Result<Unit>
}