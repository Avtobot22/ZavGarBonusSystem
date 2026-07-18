package com.zavgar.system.network.auth

import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.network.mapper.ApiException
import com.zavgar.system.network.model.LoginResponse
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException

internal class BearerTokenRefresher(
    private val sessionDataSource: SessionDataSource,
    private val onRefreshRejected: suspend (refreshToken: String) -> Unit,
) {

    suspend fun refresh(
        oldTokens: BearerTokens?,
        requestNewTokens: suspend (refreshToken: String) -> LoginResponse,
    ): BearerTokens? {
        val oldRefreshToken = oldTokens?.refreshToken ?: return null

        return try {
            val newTokens = requestNewTokens(oldRefreshToken)
            val saved = sessionDataSource.saveTokensIfRefreshTokenMatches(
                expectedRefreshToken = oldRefreshToken,
                accessToken = newTokens.accessToken,
                refreshToken = newTokens.refreshToken,
            ).getOrNull() == true

            if (saved) {
                BearerTokens(
                    accessToken = newTokens.accessToken,
                    refreshToken = newTokens.refreshToken,
                )
            } else {
                null
            }
        } catch (cause: ApiException) {
            if (cause.statusCode == HttpStatusCode.Unauthorized.value) {
                onRefreshRejected(oldRefreshToken)
            }
            null
        } catch (cause: CancellationException) {
            throw cause
        } catch (_: Exception) {
            null
        }
    }
}
