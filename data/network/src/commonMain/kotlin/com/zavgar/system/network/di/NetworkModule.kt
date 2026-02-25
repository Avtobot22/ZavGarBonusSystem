package com.zavgar.system.network.di

import com.zavgar.system.network.model.LoginResponse
import com.zavgar.system.network.remote.AuthServiceImpl
import com.zavgar.system.network.remote.LoyaltyServiceImpl
import com.zavgar.system.network.remote.UserProfileServiceImpl
import com.zavgar.system.repository.datasource.SessionDataSource
import com.zavgar.system.repository.remote.AuthService
import com.zavgar.system.repository.remote.LoyaltyService
import com.zavgar.system.repository.remote.UserProfileService
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect val BASE_URL: String

val networkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            coerceInputValues = true
            prettyPrint = true
        }
    }

    single(named("publicClient")) {
        HttpClient {
            configureCommon(get(), BASE_URL)
        }
    }

    single(named("authClient")) {
        val sessionDataSource = get<SessionDataSource>()
        val publicClient = get<HttpClient>(named("publicClient"))

        HttpClient {
            configureCommon(get(), BASE_URL)

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessResult = sessionDataSource.getAccessToken()
                        val refreshResult = sessionDataSource.getRefreshToken()

                        val access = accessResult.getOrNull()
                        val refresh = refreshResult.getOrNull()

                        if (access != null && refresh != null) {
                            BearerTokens(accessToken = access, refreshToken = refresh)
                        } else {
                            null
                        }
                    }

                    refreshTokens {

                        val oldRefreshToken =
                            sessionDataSource.getRefreshToken().getOrNull() ?: return@refreshTokens null

                        try {
                            val response = publicClient.post("auth/refresh/token") {
                                headers {
                                    append(HttpHeaders.Authorization, "Bearer $oldRefreshToken")
                                }
                            }

                            if (response.status.isSuccess()) {
                                val newTokens: LoginResponse = response.body()

                                sessionDataSource.saveTokens(
                                    accessToken = newTokens.accessToken,
                                    refreshToken = newTokens.refreshToken
                                )

                                BearerTokens(
                                    accessToken = newTokens.accessToken,
                                    refreshToken = newTokens.refreshToken
                                )
                            } else {
                                null
                            }
                        } catch (_: Exception) {
                            null
                        }
                    }
                }
            }
        }
    }

    single<AuthService> {
        AuthServiceImpl(get(named("publicClient")))
    }

    single<UserProfileService> {
        UserProfileServiceImpl(get(named("authClient")))
    }

    single<LoyaltyService> {
        LoyaltyServiceImpl(get(named("authClient")))
    }
}

private fun HttpClientConfig<*>.configureCommon(json: Json, baseUrl: String) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(json)
    }

    install(DefaultRequest) {
        url(baseUrl)
        contentType(ContentType.Application.Json)
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 15_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 10_000
    }
}
