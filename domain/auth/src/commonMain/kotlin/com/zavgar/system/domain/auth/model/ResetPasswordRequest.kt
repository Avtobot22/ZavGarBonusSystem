package com.zavgar.system.domain.auth.model

data class ResetPasswordRequest(
    val phone: String,
    val newPassword: String,
)
