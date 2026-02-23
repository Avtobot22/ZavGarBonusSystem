package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.request.LoginRequest as DomainLoginRequest
import com.zavgar.system.repository.model.request.LoginRequest as RepoLoginRequest

fun DomainLoginRequest.toRepo() = RepoLoginRequest(
    phone = this.phone,
    password = this.password
)