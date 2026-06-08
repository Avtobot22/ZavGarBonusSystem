package com.zavgar.system.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("accessExpiresIn")
    val accessExpiresIn: Long,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("refreshExpiresIn")
    val refreshExpiresIn: Long,
)
