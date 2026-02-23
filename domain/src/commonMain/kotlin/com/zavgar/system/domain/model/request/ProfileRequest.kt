package com.zavgar.system.domain.model.request

import kotlinx.datetime.LocalDate

data class ProfileRequest(
    val name: String,
    val birthDate: LocalDate,
)