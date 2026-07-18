package com.zavgar.system.network.auth

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.clearAuthTokens

interface AuthTokenCache {

    fun clear()
}

internal class KtorAuthTokenCache(
    private val client: HttpClient,
) : AuthTokenCache {

    override fun clear() {
        client.clearAuthTokens()
    }
}
