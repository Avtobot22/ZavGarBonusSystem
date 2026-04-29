package com.zavgar.system.network.di

import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.session.LogoutHandler
import com.zavgar.system.network.model.LoginResponse
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.network.remote.AuthServiceImpl
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.network.remote.LoyaltyServiceImpl
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.network.remote.UserProfileServiceImpl
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
        val koinScope = this

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
                            sessionDataSource.getRefreshToken().getOrNull()
                                ?: return@refreshTokens triggerLogout(koinScope.get())

                        try {
                            val response = publicClient.post("auth/refresh/token") {
                                headers {
                                    append(HttpHeaders.Authorization, "Bearer $oldRefreshToken")
                                }
                            }

                            if (response.status.isSuccess()) {
                                val newTokens: LoginResponse = response.body()

                                val saveResult = sessionDataSource.saveTokens(
                                    accessToken = newTokens.accessToken,
                                    refreshToken = newTokens.refreshToken
                                )
                                if (saveResult.isFailure) {
                                    return@refreshTokens triggerLogout(koinScope.get())
                                }

                                BearerTokens(
                                    accessToken = newTokens.accessToken,
                                    refreshToken = newTokens.refreshToken
                                )
                            } else {
                                triggerLogout(koinScope.get())
                            }
                        } catch (_: Exception) {
                            triggerLogout(koinScope.get())
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

private suspend fun triggerLogout(logoutHandler: LogoutHandler): BearerTokens? {
    logoutHandler.logout()
    return null
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
