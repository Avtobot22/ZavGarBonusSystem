package com.zavgar.system.authorization.model

data class LoginRequest(
    val phone: String,
    val password: String
)
