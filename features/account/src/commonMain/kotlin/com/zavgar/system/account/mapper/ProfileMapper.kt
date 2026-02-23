package com.zavgar.system.account.mapper

import com.zavgar.system.account.model.ProfileRequest as UiProfileRequest
import com.zavgar.system.account.model.ProfileResponse as UiProfileResponse
import com.zavgar.system.domain.model.response.ProfileResponse as DomainProfileResponse
import com.zavgar.system.domain.model.request.ProfileRequest as DomainProfileRequest

fun UiProfileRequest.toDomain() = DomainProfileRequest(
    name = name,
    birthDate = birthDate
)

fun DomainProfileResponse.toPresentation() = UiProfileResponse(
    name = name,
    birthDate = birthDate
)