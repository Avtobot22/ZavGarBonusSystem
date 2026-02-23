package com.zavgar.system.repository.model.request

import kotlinx.datetime.LocalDate

data class RegisterRequest(
    val name: String,
    val birthDate: LocalDate,
    val phone: String,
    val password: String,
)
