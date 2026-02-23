package com.zavgar.system.account.model

import kotlinx.datetime.LocalDate

data class ProfileResponse(
    val name: String,
    val birthDate: LocalDate,
)
