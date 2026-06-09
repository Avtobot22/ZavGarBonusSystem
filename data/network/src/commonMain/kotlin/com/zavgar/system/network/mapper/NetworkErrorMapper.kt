package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.ApiErrorCode
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException

private const val CLIENT_ERROR_RANGE_START = 400
private const val SERVER_ERROR_RANGE_START = 500
private const val SERVER_ERROR_RANGE_END = 599
private const val TOO_MANY_REQUESTS_STATUS = 429

sealed class NetworkErrorKind {
    data class Client(
        val statusCode: Int,
        val message: String,
        val errorCode: ApiErrorCode? = null,
        val retryAfterSeconds: Long? = null,
    ) : NetworkErrorKind()

    data object Server : NetworkErrorKind()
    data object Network : NetworkErrorKind()
    data class Unknown(val message: String) : NetworkErrorKind()
}

fun Throwable.classifyNetworkError(): NetworkErrorKind = when (this) {
    is ApiException -> when (statusCode) {
        in SERVER_ERROR_RANGE_START..SERVER_ERROR_RANGE_END -> NetworkErrorKind.Server
        in CLIENT_ERROR_RANGE_START until SERVER_ERROR_RANGE_START ->
            NetworkErrorKind.Client(statusCode, errorMessage, errorCode, retryAfterSeconds)

        else -> NetworkErrorKind.Unknown(errorMessage)
    }

    is ClientRequestException -> NetworkErrorKind.Client(response.status.value, message)
    is ServerResponseException -> NetworkErrorKind.Server
    is IOException -> NetworkErrorKind.Network
    else -> NetworkErrorKind.Unknown(message ?: "Unknown error")
}

/**
 * Доменный лимит частоты запросов: либо машиночитаемый код `TOO_MANY_REQUESTS`,
 * либо фреймворковый rate-limit (статус 429 с пустым телом и заголовком `Retry-After`).
 */
fun NetworkErrorKind.Client.isTooManyRequests(): Boolean =
    errorCode == ApiErrorCode.TOO_MANY_REQUESTS || statusCode == TOO_MANY_REQUESTS_STATUS
