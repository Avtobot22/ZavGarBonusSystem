package com.zavgar.system.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResendRequest(
    @SerialName("phone")
    val phone: String
)
