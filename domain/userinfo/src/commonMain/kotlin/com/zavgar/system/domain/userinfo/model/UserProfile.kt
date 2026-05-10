package com.zavgar.system.domain.userinfo.model

import kotlinx.datetime.LocalDate

data class UserProfile(
    val name: String,
    val phone: String,
    val birthDate: LocalDate,
)
