package com.zavgar.system.domain.model.response

data class Session(
    val phone: String,
    val accessToken: String,
    val refreshToken: String
)
