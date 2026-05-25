package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.datastore.model.Session
import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError
import com.zavgar.system.network.model.ConfirmationRequest
import com.zavgar.system.network.model.LoginRequest
import com.zavgar.system.network.model.RegisterRequest
import com.zavgar.system.network.model.ResendRequest
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

internal class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionDataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : AuthRepository {

    override suspend fun login(phone: String): AppResult<Unit, AuthError> =
        withContext(dispatcherProvider.io) {
            authService.loginRequest(LoginRequest(phone = phone))
                .toAppResult { it.toAuthError() }
        }

    override suspend fun register(
        name: String,
        birthDate: LocalDate,
        phone: String,
    ): AppResult<Unit, RegisterError> = withContext(dispatcherProvider.io) {
        authService.registerRequest(
            RegisterRequest(name = name, birthDate = birthDate, phone = phone)
        ).toAppResult { it.toRegisterError() }
    }

    override suspend fun confirmLogin(phone: String, code: String): AppResult<Unit, ConfirmationError> =
        withContext(dispatcherProvider.io) {
            val response = authService.confirmLogin(ConfirmationRequest(phone = phone, code = code))
                .fold(
                    onSuccess = { it },
                    onFailure = { return@withContext AppResult.Error(it.toConfirmationError()) },
                )
            val saved = sessionDataSource.saveSession(Session(response.accessToken, response.refreshToken, phone))
            saved.fold(
                onSuccess = { AppResult.Success(Unit) },
                onFailure = { AppResult.Error(ConfirmationError.UnknownError(it.message ?: "Storage Error")) },
            )
        }

    override suspend fun confirmRegistration(phone: String, code: String): AppResult<Unit, ConfirmationError> =
        withContext(dispatcherProvider.io) {
            authService.confirmRegistration(ConfirmationRequest(phone = phone, code = code))
                .toAppResult { it.toConfirmationError() }
        }

    override suspend fun resendCode(phone: String): AppResult<Unit, ResendConfirmationError> =
        withContext(dispatcherProvider.io) {
            authService.resendCode(ResendRequest(phone = phone))
                .toAppResult { it.toResendConfirmationError() }
        }

    private fun Throwable.toAuthError(): AuthError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            400 -> AuthError.ValidationError
            404 -> AuthError.UserNotFound
            429 -> AuthError.TooManyRequestError
            else -> AuthError.UnknownError(kind.message)
        }

        is NetworkErrorKind.Server -> AuthError.ServerError
        is NetworkErrorKind.Network -> AuthError.NetworkError
        is NetworkErrorKind.Unknown -> AuthError.UnknownError(kind.message)
    }

    private fun Throwable.toRegisterError(): RegisterError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            400 -> RegisterError.InvalidFormat
            409 -> RegisterError.UserAlreadyExists
            429 -> RegisterError.TooManyRequestError
            else -> RegisterError.UnknownError(kind.message)
        }

        is NetworkErrorKind.Server -> RegisterError.ServerError
        is NetworkErrorKind.Network -> RegisterError.NetworkError
        is NetworkErrorKind.Unknown -> RegisterError.UnknownError(kind.message)
    }

    private fun Throwable.toConfirmationError(): ConfirmationError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            400 -> ConfirmationError.InvalidCodeError
            429 -> ConfirmationError.TooManyRequestError
            else -> ConfirmationError.UnknownError(kind.message)
        }

        is NetworkErrorKind.Server -> ConfirmationError.ServerError
        is NetworkErrorKind.Network -> ConfirmationError.NetworkError
        is NetworkErrorKind.Unknown -> ConfirmationError.UnknownError(kind.message)
    }

    private fun Throwable.toResendConfirmationError(): ResendConfirmationError =
        when (val kind = classifyNetworkError()) {
            is NetworkErrorKind.Client -> when (kind.statusCode) {
                400 -> ResendConfirmationError.InvalidPhone
                429 -> ResendConfirmationError.TooManyRequestError
                else -> ResendConfirmationError.UnknownError(kind.message)
            }

            is NetworkErrorKind.Server -> ResendConfirmationError.ServerError
            is NetworkErrorKind.Network -> ResendConfirmationError.NetworkError
            is NetworkErrorKind.Unknown -> ResendConfirmationError.UnknownError(kind.message)
        }
}
