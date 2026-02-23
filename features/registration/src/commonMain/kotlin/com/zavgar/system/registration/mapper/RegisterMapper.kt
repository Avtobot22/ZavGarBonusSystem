package com.zavgar.system.registration.mapper

import com.zavgar.system.domain.model.request.RegisterRequest as DomainRegisterRequest
import com.zavgar.system.registration.model.RegisterRequest as PresentationRegisterRequest

fun PresentationRegisterRequest.toDomain() = DomainRegisterRequest(
    name = name,
    birthDate = birthDate,
    phone = phone,
    password = password
)
