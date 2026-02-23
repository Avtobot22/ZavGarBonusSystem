package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.request.RegisterRequest as DomainRegisterRequest
import com.zavgar.system.repository.model.request.RegisterRequest as RepoRegisterRequest

fun DomainRegisterRequest.toRepo() = RepoRegisterRequest(
    name = this.name,
    birthDate = this.birthDate,
    phone = this.phone,
    password = this.password
)