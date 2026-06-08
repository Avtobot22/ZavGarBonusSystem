package com.zavgar.system.network.di

import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.session.LogoutHandler
import com.zavgar.system.firebase.config.RemoteConfigService
import com.zavgar.system.firebase.di.IS_DEBUG_BUILD
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
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
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
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val REQUEST_TIMEOUT_MILLIS = 15_000L
private const val CONNECT_TIMEOUT_MILLIS = 10_000L
private const val SOCKET_TIMEOUT_MILLIS = 10_000L

val networkModule = module {

    single {
        val isDebugBuild = get<Boolean>(named(IS_DEBUG_BUILD))
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            coerceInputValues = true
            prettyPrint = isDebugBuild
        }
    }

    single(named("publicClient")) {
        val isDebugBuild = get<Boolean>(named(IS_DEBUG_BUILD))
        val baseUrl = get<RemoteConfigService>().baseUrl

        HttpClient {
            configureCommon(get(), baseUrl, isDebugBuild)
        }
    }

    single(named("authClient")) {
        val sessionDataSource = get<SessionDataSource>()
        val publicClient = get<HttpClient>(named("publicClient"))
        val isDebugBuild = get<Boolean>(named(IS_DEBUG_BUILD))
        val baseUrl = get<RemoteConfigService>().baseUrl
        val koinScope = this

        HttpClient {
            configureCommon(get(), baseUrl, isDebugBuild)

            install(Auth) {
                bearer {
                    loadTokens {
                        val access = sessionDataSource.getAccessToken().getOrNull()
                        val refresh = sessionDataSource.getRefreshToken().getOrNull()

                        if (access != null && refresh != null) {
                            BearerTokens(accessToken = access, refreshToken = refresh)
                        } else {
                            null
                        }
                    }

                    sendWithoutRequest { true }

                    refreshTokens {
                        val oldRefreshToken = oldTokens?.refreshToken
                            ?: sessionDataSource.getRefreshToken().getOrNull()
                            ?: return@refreshTokens null

                        try {
                            val newTokens: LoginResponse = publicClient.post("auth/refresh/token") {
                                markAsRefreshTokenRequest()
                                headers {
                                    append(HttpHeaders.Authorization, "Bearer $oldRefreshToken")
                                }
                            }.body()

                            sessionDataSource.saveTokens(
                                accessToken = newTokens.accessToken,
                                refreshToken = newTokens.refreshToken,
                            )

                            BearerTokens(
                                accessToken = newTokens.accessToken,
                                refreshToken = newTokens.refreshToken,
                            )
                        } catch (cause: ResponseException) {
                            if (cause.response.status.isAuthFailure()) {
                                triggerLogout(koinScope.get())
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

private suspend fun triggerLogout(logoutHandler: LogoutHandler): BearerTokens? {
    logoutHandler.logout()
    return null
}

private fun HttpStatusCode.isAuthFailure(): Boolean =
    this == HttpStatusCode.Unauthorized || this == HttpStatusCode.Forbidden

private fun HttpClientConfig<*>.configureCommon(
    json: Json,
    baseUrl: String,
    isDebugBuild: Boolean,
) {
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
        level = if (isDebugBuild) LogLevel.ALL else LogLevel.NONE
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
        socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
    }

    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 3)
        retryOnException(maxRetries = 3, retryOnTimeout = false)
        exponentialDelay()
    }
}
