package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.io.IOException

/**
 * Dispatcher provider for tests: every dispatcher is [Dispatchers.Unconfined],
 * so `withContext(provider.io)` runs inline within `runTest`.
 */
class TestDispatcherProvider : CoroutineDispatcherProvider {
    override val io = Dispatchers.Unconfined
    override val main = Dispatchers.Unconfined
    override val default = Dispatchers.Unconfined
}

/** A Ktor [ClientRequestException] whose status and message are stubbed for error-mapping tests. */
fun clientError(statusCode: Int, message: String = "client error"): ClientRequestException {
    val exception = mockk<ClientRequestException>()
    every { exception.response } returns mockk {
        every { status } returns HttpStatusCode.fromValue(statusCode)
    }
    every { exception.message } returns message
    return exception
}

/** A Ktor [ServerResponseException] (classified as a server error). */
fun serverError(): ServerResponseException = mockk()

/** An IO failure (classified as a network error). */
fun networkError(message: String = "no connection"): IOException = IOException(message)
