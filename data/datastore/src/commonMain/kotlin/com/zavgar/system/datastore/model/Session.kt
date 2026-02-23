package com.zavgar.system.datastore.model

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val phone: String
)
