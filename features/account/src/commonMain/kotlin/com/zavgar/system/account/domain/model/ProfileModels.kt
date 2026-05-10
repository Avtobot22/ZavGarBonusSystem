package com.zavgar.system.account.domain.model

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
