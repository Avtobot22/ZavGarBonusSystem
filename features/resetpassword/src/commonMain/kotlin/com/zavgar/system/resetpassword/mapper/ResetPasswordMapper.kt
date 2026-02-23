package com.zavgar.system.resetpassword.mapper

import com.zavgar.system.domain.model.request.ResetPasswordRequest as DomainResetPasswordRequest
import com.zavgar.system.resetpassword.model.ResetPasswordRequest as PresentationResetPasswordRequest

fun PresentationResetPasswordRequest.toDomain() = DomainResetPasswordRequest(
    phone = phone,
    password = newPassword
)