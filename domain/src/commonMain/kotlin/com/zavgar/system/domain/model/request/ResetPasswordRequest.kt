package com.zavgar.system.domain.model.request

data class ResetPasswordRequest(
    val phone: String,
    val password: String
)
