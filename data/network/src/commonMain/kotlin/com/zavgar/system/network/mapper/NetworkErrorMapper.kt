package com.zavgar.system.network.mapper

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException

sealed class NetworkErrorKind {
    data class Client(val statusCode: Int, val message: String) : NetworkErrorKind()
    data object Server : NetworkErrorKind()
    data object Network : NetworkErrorKind()
    data class Unknown(val message: String) : NetworkErrorKind()
}

fun Throwable.classifyNetworkError(): NetworkErrorKind = when (this) {
    is ClientRequestException -> NetworkErrorKind.Client(response.status.value, message)
    is ServerResponseException -> NetworkErrorKind.Server
    is IOException -> NetworkErrorKind.Network
    else -> NetworkErrorKind.Unknown(message ?: "Unknown error")
}
