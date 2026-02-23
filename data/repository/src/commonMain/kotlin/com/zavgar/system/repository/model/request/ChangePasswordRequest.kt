package com.zavgar.system.repository.model.request

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
