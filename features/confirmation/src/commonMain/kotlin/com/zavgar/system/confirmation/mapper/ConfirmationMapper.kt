package com.zavgar.system.confirmation.mapper

import com.zavgar.system.confirmation.model.ConfirmationRequest as PresentationConfirmationRequest
import com.zavgar.system.confirmation.model.ResendRequest as PresentationResendRequest
import com.zavgar.system.domain.model.request.ConfirmationRequest as DomainConfirmationRequest
import com.zavgar.system.domain.model.request.ResendRequest as DomainResendRequest

fun PresentationConfirmationRequest.toDomain() = DomainConfirmationRequest(
    phone = phone,
    code = code,
    isRegistration = isRegistration
)

fun PresentationResendRequest.toDomain() = DomainResendRequest(
    phone = phone
)