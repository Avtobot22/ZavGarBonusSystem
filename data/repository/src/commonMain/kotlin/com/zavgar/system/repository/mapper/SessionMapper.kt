package com.zavgar.system.repository.mapper

import com.zavgar.system.repository.model.response.LoginResponse
import com.zavgar.system.domain.model.response.Session as DomainSession
import com.zavgar.system.repository.model.response.Session as RepoSession


fun LoginResponse.toSession(phone: String) = RepoSession(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    phone = phone
)

fun RepoSession.toDomain() = DomainSession(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    phone = this.phone
)

fun Result<RepoSession>.toDomain(): Result<DomainSession> =
    this.map { it.toDomain() }