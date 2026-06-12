package com.zavgar.system.network.di

import com.zavgar.system.config.AppConfig
import com.zavgar.system.config.IS_DEBUG_BUILD
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.session.LogoutHandler
import com.zavgar.system.network.mapper.ApiException
import com.zavgar.system.network.model.ApiErrorCode
import com.zavgar.system.network.model.ErrorResponse
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
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
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
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val REQUEST_TIMEOUT_MILLIS = 15_000L
private const val CONNECT_TIMEOUT_MILLIS = 10_000L
private const val SOCKET_TIMEOUT_MILLIS = 10_000L
private const val MAX_RETRIES = 3
private const val SERVER_ERROR_RANGE_END = 599

/**
 * Имя Koin-квалификатора для опционального [HttpClientEngine] сетевого слоя.
 *
 * Если модуль с этим биндингом подмешан в граф (см. `:data:network-mock`), оба
 * `HttpClient` собираются на нём — приложение работает на mock-сервере без реального бэкенда.
 * Если биндинга нет ([Scope.getOrNull] вернёт `null`), используется дефолтный платформенный
 * движок (OkHttp на Android, Darwin на iOS) — обычный рабочий режим.
 */
const val NETWORK_ENGINE: String = "networkEngine"

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
        val appConfig = get<AppConfig>()

        buildHttpClient(getOrNull(named(NETWORK_ENGINE))) {
            configureCommon(get(), appConfig::baseUrl, isDebugBuild)
        }
    }

    single(named("authClient")) {
        val sessionDataSource = get<SessionDataSource>()
        val publicClient = get<HttpClient>(named("publicClient"))
        val isDebugBuild = get<Boolean>(named(IS_DEBUG_BUILD))
        val appConfig = get<AppConfig>()
        val koinScope = this

        buildHttpClient(getOrNull(named(NETWORK_ENGINE))) {
            configureCommon(get(), appConfig::baseUrl, isDebugBuild)

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
                        } catch (cause: ApiException) {
                            if (cause.statusCode.isRefreshAuthFailure()) {
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

private fun Int.isRefreshAuthFailure(): Boolean = this == HttpStatusCode.Unauthorized.value

/**
 * Создаёт [HttpClient] на переданном [engine], либо на дефолтном платформенном движке,
 * если [engine] равен `null`. Так сетевой слой остаётся одним и тем же, а подменяется
 * только транспорт — это и позволяет подключить mock-сервер без изменения сервисов.
 */
private fun buildHttpClient(
    engine: HttpClientEngine?,
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient = if (engine != null) HttpClient(engine, block) else HttpClient(block)

private fun HttpClientConfig<*>.configureCommon(
    json: Json,
    baseUrlProvider: () -> String,
    isDebugBuild: Boolean,
) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(json)
    }

    install(DefaultRequest) {
        url(baseUrlProvider())
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
        maxRetries = MAX_RETRIES
        retryIf { request, response ->
            request.method == HttpMethod.Get &&
                response.status.value in HttpStatusCode.InternalServerError.value..SERVER_ERROR_RANGE_END
        }
        retryOnExceptionIf { request, cause ->
            request.method == HttpMethod.Get &&
                cause !is HttpRequestTimeoutException &&
                cause !is CancellationException
        }
        exponentialDelay()
    }

    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, _ ->
            val responseException = exception as? ResponseException ?: return@handleResponseExceptionWithRequest
            throw responseException.response.toApiException(responseException)
        }
    }
}

private suspend fun HttpResponse.toApiException(cause: ResponseException): ApiException {
    val retryAfterSeconds = headers[HttpHeaders.RetryAfter]?.toLongOrNull()
    val errorBody = runCatching { body<ErrorResponse>() }.getOrNull()
    return ApiException(
        statusCode = status.value,
        errorCode = ApiErrorCode.fromRaw(errorBody?.code),
        retryAfterSeconds = retryAfterSeconds,
        errorMessage = errorBody?.message ?: cause.message.orEmpty(),
        cause = cause,
    )
}
