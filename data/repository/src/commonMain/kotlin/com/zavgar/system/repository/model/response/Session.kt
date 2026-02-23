package com.zavgar.system.repository.model.response

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val phone: String
)