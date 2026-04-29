package com.zavgar.system.domain.session

data class Session(
    val phone: String,
    val accessToken: String,
    val refreshToken: String,
)
