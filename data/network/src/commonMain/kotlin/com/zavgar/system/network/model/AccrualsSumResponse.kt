package com.zavgar.system.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccrualsSumResponse(
    @SerialName("sum")
    val sum: Int,
)
