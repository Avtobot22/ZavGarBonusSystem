package com.zavgar.system.network.model

import com.zavgar.system.core.serializer.LocalDateDDMMYYYYSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    @SerialName("name")
    val name: String,
    @SerialName("birthDate")
    @Serializable(with = LocalDateDDMMYYYYSerializer::class)
    val birthDate: LocalDate,
    @SerialName("phone")
    val phone: String,
)
