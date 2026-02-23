package com.zavgar.system.domain.model.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
