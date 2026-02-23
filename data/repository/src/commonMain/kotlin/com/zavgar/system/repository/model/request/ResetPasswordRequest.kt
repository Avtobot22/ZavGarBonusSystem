package com.zavgar.system.repository.model.request

data class ResetPasswordRequest(
    val phone: String,
    val password: String
)
