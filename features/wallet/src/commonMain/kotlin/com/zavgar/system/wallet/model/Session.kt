package com.zavgar.system.wallet.model

data class Session(
    val phone: String,
    val accessToken: String,
    val refreshToken: String
)
