package com.zavgar.system.domain.model.response

import kotlinx.datetime.LocalDate

data class ProfileResponse(
    val name: String,
    val birthDate: LocalDate,
)
