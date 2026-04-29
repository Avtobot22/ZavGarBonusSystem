package com.zavgar.system.authorization.domain.usecase

import com.zavgar.system.authorization.domain.error.AuthError
import com.zavgar.system.authorization.domain.error.toAuthError
import com.zavgar.system.authorization.domain.model.LoginRequest
import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.datastore.model.Session
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.LoginRequest as NetworkLoginRequest

class LoginUseCase(
    private val authService: AuthService,
    private val sessionDataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(loginRequest: LoginRequest): AppResult<Unit, AuthError> =
        withContext(dispatcherProvider.io) {
            val response = when (
                val result = authService
                    .loginRequest(NetworkLoginRequest(phone = loginRequest.phone, password = loginRequest.password))
                    .toAppResult(Throwable::toAuthError)
            ) {
                is AppResult.Error -> return@withContext result
                is AppResult.Success -> result.data
            }

            sessionDataSource.saveSession(
                Session(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    phone = loginRequest.phone,
                )
            ).onFailure { exception ->
                return@withContext AppResult.Error(
                    AuthError.UnknownError(exception.message ?: "Storage Error")
                )
            }

            AppResult.Success(Unit)
        }
}
