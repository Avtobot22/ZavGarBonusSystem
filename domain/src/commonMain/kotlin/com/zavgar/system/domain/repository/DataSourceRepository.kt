package com.zavgar.system.domain.repository

import com.zavgar.system.domain.model.response.Session

interface DataSourceRepository {
    suspend fun getSession(): Result<Session>

    suspend fun deleteSession(): Result<Unit>
}