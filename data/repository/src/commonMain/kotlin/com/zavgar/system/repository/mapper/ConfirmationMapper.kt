package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.request.ConfirmationRequest as DomainConfirmationRequest
import com.zavgar.system.domain.model.request.ResendRequest as DomainResendRequest
import com.zavgar.system.repository.model.request.ConfirmationRequest as RepoConfirmationRequest
import com.zavgar.system.repository.model.request.ResendRequest as RepoResendRequest

fun DomainConfirmationRequest.toRepo() = RepoConfirmationRequest(
    phone = this.phone,
    code = this.code
)

fun DomainResendRequest.toRepo() = RepoResendRequest(
    phone = this.phone
)