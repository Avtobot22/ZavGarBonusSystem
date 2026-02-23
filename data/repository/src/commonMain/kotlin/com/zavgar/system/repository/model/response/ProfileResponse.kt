package com.zavgar.system.repository.model.response

import kotlinx.datetime.LocalDate

data class ProfileResponse(
    val name: String,
    val phone: String,
    val birthDate: LocalDate
)
