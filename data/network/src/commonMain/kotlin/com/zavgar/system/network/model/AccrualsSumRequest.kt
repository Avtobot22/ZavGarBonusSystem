package com.zavgar.system.network.model

import com.zavgar.system.core.serializer.LocalDateDDMMYYYYSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccrualsSumRequest(
    @SerialName("periodStart")
    @Serializable(with = LocalDateDDMMYYYYSerializer::class)
    val periodStart: LocalDate? = null,
    @SerialName("periodEnd")
    @Serializable(with = LocalDateDDMMYYYYSerializer::class)
    val periodEnd: LocalDate? = null,
)
