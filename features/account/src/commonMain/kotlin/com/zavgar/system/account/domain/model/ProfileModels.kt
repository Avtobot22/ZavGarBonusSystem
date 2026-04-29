package com.zavgar.system.account.domain.model

import kotlinx.datetime.LocalDate

data class ProfileRequest(
    val name: String,
    val birthDate: LocalDate,
)

data class ProfileResponse(
    val name: String,
    val birthDate: LocalDate,
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)
