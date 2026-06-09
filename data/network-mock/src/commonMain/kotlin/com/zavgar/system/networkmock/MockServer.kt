package com.zavgar.system.networkmock

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

/**
 * Ktor [MockEngine], заменяющий реальный сервер заготовленными ответами ([MockResponses]).
 *
 * Маршрутизация — по HTTP-методу и суффиксу пути ([endsWith]), чтобы не зависеть от базового
 * URL (`baseUrl` всё ещё проставляется `DefaultRequest`, но движок его игнорирует). Любой
 * нераспознанный запрос отдаёт 404 с телом `ErrorResponse`, что упрощает отладку.
 *
 * Ответы возвращаются как `application/json`; `expectSuccess`/`ContentNegotiation` из
 * `networkModule` обрабатывают их так же, как ответы реального бэкенда.
 */
internal fun createMockEngine(): MockEngine = MockEngine { request ->
    val path = request.url.encodedPath
    val method = request.method

    when {
        method.isPost(path, "auth/login") -> okEmpty()
        method.isPost(path, "auth/register") -> okEmpty()
        method.isPost(path, "auth/confirm/login") -> okJson(MockResponses.loginResponse)
        method.isPost(path, "auth/confirm/register") -> okEmpty()
        method.isPost(path, "auth/refresh/code") -> okEmpty()
        method.isPost(path, "auth/refresh/token") -> okJson(MockResponses.loginResponse)

        method.matches(HttpMethod.Get, path, "auth/profile") -> okJson(MockResponses.profileResponse)
        method.matches(HttpMethod.Put, path, "auth/profile") -> okEmpty()
        method.matches(HttpMethod.Delete, path, "auth/logout") -> okEmpty()
        method.matches(HttpMethod.Delete, path, "auth/delete") -> okEmpty()

        method.matches(HttpMethod.Get, path, "users/me/balance") -> okJson(MockResponses.balanceResponse)
        method.isPost(path, "users/me/operations") -> okJson(MockResponses.transactionsPageResponse)
        method.isPost(path, "users/me/accruals/sum") -> okJson(MockResponses.accrualsSumResponse)

        else -> notFound(method, path)
    }
}

private fun HttpMethod.isPost(path: String, suffix: String): Boolean =
    matches(HttpMethod.Post, path, suffix)

private fun HttpMethod.matches(expected: HttpMethod, path: String, suffix: String): Boolean =
    this == expected && path.trimEnd('/').endsWith(suffix)

private fun MockRequestHandleScope.okJson(body: String): HttpResponseData =
    respond(
        content = body,
        status = HttpStatusCode.OK,
        headers = jsonHeaders,
    )

private fun MockRequestHandleScope.okEmpty(): HttpResponseData =
    respond(
        content = ByteReadChannel(ByteArray(0)),
        status = HttpStatusCode.OK,
    )

private fun MockRequestHandleScope.notFound(method: HttpMethod, path: String): HttpResponseData =
    respond(
        content = MockResponses.errorBody(
            code = "NOT_FOUND",
            message = "Mock server has no stub for ${method.value} $path",
        ),
        status = HttpStatusCode.NotFound,
        headers = jsonHeaders,
    )

private val jsonHeaders = headersOf("Content-Type", "application/json")
