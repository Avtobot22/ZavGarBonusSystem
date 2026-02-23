package com.zavgar.system.account.mapper

import com.zavgar.system.account.model.ChangePasswordRequest as UiChangePasswordRequest
import com.zavgar.system.domain.model.request.ChangePasswordRequest as DomainChangePasswordRequest

fun UiChangePasswordRequest.toDomain() = DomainChangePasswordRequest(
    oldPassword = oldPassword,
    newPassword = newPassword
)