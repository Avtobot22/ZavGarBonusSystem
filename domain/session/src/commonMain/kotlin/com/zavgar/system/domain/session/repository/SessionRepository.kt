package com.zavgar.system.domain.session.repository

import com.zavgar.system.domain.session.error.SessionError
import com.zavgar.system.domain.session.model.Session
import com.zavgar.system.utils.result.AppResult

interface SessionRepository {

    suspend fun getSession(): AppResult<Session, SessionError>

    suspend fun deleteSession(): AppResult<Unit, SessionError>

    suspend fun deleteSessionIfRefreshTokenMatches(refreshToken: String): AppResult<Boolean, SessionError>
}
