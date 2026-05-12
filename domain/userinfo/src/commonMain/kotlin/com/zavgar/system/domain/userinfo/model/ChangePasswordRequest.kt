package com.zavgar.system.domain.userinfo.model

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
