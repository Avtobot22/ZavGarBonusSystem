package com.zavgar.system.repository.mapper

import com.zavgar.system.domain.model.response.ProfileResponse as DomainProfileResponse
import com.zavgar.system.domain.model.request.ProfileRequest as DomainProfileRequest
import com.zavgar.system.repository.model.request.ProfileRequest as RepoProfileRequest
import com.zavgar.system.repository.model.response.ProfileResponse as RepoProfileResponse

fun DomainProfileRequest.toRepo() = RepoProfileRequest(
    name = name,
    birthDate = birthDate
)

fun RepoProfileResponse.toDomain() = DomainProfileResponse(
    name = name,
    birthDate = birthDate
)