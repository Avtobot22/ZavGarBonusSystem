package com.zavgar.system.domain.auth.model

import kotlinx.datetime.LocalDate

data class RegisterRequest(
    val name: String,
    val birthDate: LocalDate,
    val phone: String,
    val password: String,
)
