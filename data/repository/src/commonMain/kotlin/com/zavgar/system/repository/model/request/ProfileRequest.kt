package com.zavgar.system.repository.model.request

import kotlinx.datetime.LocalDate

data class ProfileRequest(
    val name: String,
    val birthDate: LocalDate
)
