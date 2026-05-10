package com.zavgar.system.domain.userinfo.model

import kotlinx.datetime.LocalDate

data class UpdateProfileRequest(
    val name: String,
    val birthDate: LocalDate,
)
