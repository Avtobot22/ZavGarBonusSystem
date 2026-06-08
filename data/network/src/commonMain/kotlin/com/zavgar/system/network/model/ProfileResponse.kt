package com.zavgar.system.network.model

import com.zavgar.system.core.serializer.LocalDateDDMMYYYYSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    @SerialName("name")
    val name: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("birthDate")
    @Serializable(with = LocalDateDDMMYYYYSerializer::class)
    val birthDate: LocalDate,
)
