package com.zavgar.system.authorization.mapper

import com.zavgar.system.authorization.model.LoginRequest as PresentationLoginRequest
import com.zavgar.system.domain.model.request.LoginRequest as DomainLoginRequest

fun PresentationLoginRequest.toDomain() = DomainLoginRequest(
    phone = phone,
    password = password
)