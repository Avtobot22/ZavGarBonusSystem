package com.zavgar.system.account.model

import kotlinx.datetime.LocalDate

data class ProfileRequest(
    val name: String,
    val birthDate: LocalDate
)
