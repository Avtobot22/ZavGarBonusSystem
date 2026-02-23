package com.zavgar.system.repository.model.response

data class LoginResponse(
    val accessToken: String,
    val accessExpiresIn: Long,
    val refreshToken: String,
    val refreshExpiresIn: Long
)