package com.zavgar.system.authorization.domain.model

data class LoginRequest(
    val phone: String,
    val password: String,
)
