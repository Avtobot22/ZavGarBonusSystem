package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.LoginRequest as NetworkLoginRequest
import com.zavgar.system.network.model.LoginResponse as NetworkLoginResponse
import com.zavgar.system.repository.model.request.LoginRequest as RepoLoginRequest
import com.zavgar.system.repository.model.response.LoginResponse as RepoLoginResponse

fun NetworkLoginResponse.toRepo() = RepoLoginResponse(
    accessToken = accessToken,
    accessExpiresIn = accessExpiresIn,
    refreshToken = refreshToken,
    refreshExpiresIn = refreshExpiresIn
)

fun RepoLoginRequest.toNetwork() = NetworkLoginRequest(
    phone = "+7${this.phone}",
    password = this.password
)