package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.request.ResetPasswordRequest as DomainResenPasswordRequest
import com.zavgar.system.repository.model.request.ResetPasswordRequest as RepoResetPasswordRequest

fun DomainResenPasswordRequest.toRepo() = RepoResetPasswordRequest(
    phone = phone,
    password = password
)