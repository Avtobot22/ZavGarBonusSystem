package com.zavgar.system.resetpassword.model

data class ResetPasswordRequest(
    val phone: String,
    val newPassword: String
)
