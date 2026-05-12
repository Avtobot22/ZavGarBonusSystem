package com.zavgar.system.domain.session.model

data class Session(
    val phone: String,
    val accessToken: String,
    val refreshToken: String,
)