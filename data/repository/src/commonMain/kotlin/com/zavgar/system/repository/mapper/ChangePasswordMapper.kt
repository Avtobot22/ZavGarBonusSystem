package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.request.ChangePasswordRequest as DomainChangePasswordRequest
import com.zavgar.system.repository.model.request.ChangePasswordRequest as RepoChangePasswordRequest

fun DomainChangePasswordRequest.toRepo() = RepoChangePasswordRequest(
    oldPassword = oldPassword,
    newPassword = newPassword
)