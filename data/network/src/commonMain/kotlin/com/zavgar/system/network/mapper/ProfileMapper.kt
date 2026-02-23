package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.ProfileRequest as NetworkProfileRequest
import com.zavgar.system.network.model.ProfileResponse as NetworkProfileResponse
import com.zavgar.system.repository.model.request.ProfileRequest as RepoProfileRequest
import com.zavgar.system.repository.model.response.ProfileResponse as RepoProfileResponse

fun NetworkProfileResponse.toRepo() = RepoProfileResponse(
    name = name,
    phone = phone,
    birthDate = birthDate
)

fun RepoProfileRequest.toNetwork() = NetworkProfileRequest(
    name = name,
    birthDate = birthDate
)