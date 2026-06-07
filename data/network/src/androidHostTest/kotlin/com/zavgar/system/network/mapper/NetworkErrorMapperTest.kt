package com.zavgar.system.network.mapper

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkErrorMapperTest {

    @Test
    fun `classifies ClientRequestException as Client with its status code and message`() {
        val exception = mockk<ClientRequestException>()
        every { exception.response } returns mockk {
            every { status } returns HttpStatusCode.fromValue(404)
        }
        every { exception.message } returns "not found"

        val result = exception.classifyNetworkError()

        assertEquals(NetworkErrorKind.Client(statusCode = 404, message = "not found"), result)
    }

    @Test
    fun `classifies ServerResponseException as Server`() {
        val exception = mockk<ServerResponseException>()

        val result = exception.classifyNetworkError()

        assertEquals(NetworkErrorKind.Server, result)
    }

    @Test
    fun `classifies IOException as Network`() {
        val result = IOException("connection reset").classifyNetworkError()

        assertEquals(NetworkErrorKind.Network, result)
    }

    @Test
    fun `classifies an arbitrary throwable as Unknown with its message`() {
        val result = IllegalStateException("boom").classifyNetworkError()

        assertEquals(NetworkErrorKind.Unknown("boom"), result)
    }

    @Test
    fun `falls back to a default message when the throwable has none`() {
        val result = Throwable().classifyNetworkError()

        assertEquals(NetworkErrorKind.Unknown("Unknown error"), result)
    }
}
