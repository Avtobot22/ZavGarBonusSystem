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
import com.zavgar.system.network.model.ApiErrorCode
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
            RegisterRequest(name = name, birthDate = birthDate, phone = phone),
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
        is NetworkErrorKind.Client -> kind.toAuthError()
        is NetworkErrorKind.Server -> AuthError.ServerError
        is NetworkErrorKind.Network -> AuthError.NetworkError
        is NetworkErrorKind.Unknown -> AuthError.UnknownError(kind.message)
    }

    private fun NetworkErrorKind.Client.toAuthError(): AuthError = when (errorCode) {
        ApiErrorCode.NOT_FOUND -> AuthError.UserNotFound
        ApiErrorCode.TOO_MANY_REQUESTS -> AuthError.TooManyRequestError(retryAfterSeconds)
        ApiErrorCode.VALIDATION_ERROR,
        ApiErrorCode.INVALID_CREDENTIALS,
        ApiErrorCode.INVALID_FORMAT,
        ApiErrorCode.INVALID_ARGUMENT,
        -> AuthError.ValidationError

        else -> when (statusCode) {
            HttpStatusCodes.NOT_FOUND -> AuthError.UserNotFound
            HttpStatusCodes.BAD_REQUEST -> AuthError.ValidationError
            HttpStatusCodes.TOO_MANY_REQUESTS -> AuthError.TooManyRequestError(retryAfterSeconds)
            else -> AuthError.UnknownError(message)
        }
    }

    private fun Throwable.toRegisterError(): RegisterError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> kind.toRegisterError()
        is NetworkErrorKind.Server -> RegisterError.ServerError
        is NetworkErrorKind.Network -> RegisterError.NetworkError
        is NetworkErrorKind.Unknown -> RegisterError.UnknownError(kind.message)
    }

    private fun NetworkErrorKind.Client.toRegisterError(): RegisterError = when (errorCode) {
        ApiErrorCode.ALREADY_EXISTS, ApiErrorCode.DUPLICATE_RESOURCE -> RegisterError.UserAlreadyExists
        ApiErrorCode.TOO_MANY_REQUESTS -> RegisterError.TooManyRequestError(retryAfterSeconds)
        ApiErrorCode.VALIDATION_ERROR,
        ApiErrorCode.INVALID_FORMAT,
        ApiErrorCode.INVALID_ARGUMENT,
        -> RegisterError.InvalidFormat

        else -> when (statusCode) {
            HttpStatusCodes.CONFLICT -> RegisterError.UserAlreadyExists
            HttpStatusCodes.BAD_REQUEST -> RegisterError.InvalidFormat
            HttpStatusCodes.TOO_MANY_REQUESTS -> RegisterError.TooManyRequestError(retryAfterSeconds)
            else -> RegisterError.UnknownError(message)
        }
    }

    private fun Throwable.toConfirmationError(): ConfirmationError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> kind.toConfirmationError()
        is NetworkErrorKind.Server -> ConfirmationError.ServerError
        is NetworkErrorKind.Network -> ConfirmationError.NetworkError
        is NetworkErrorKind.Unknown -> ConfirmationError.UnknownError(kind.message)
    }

    private fun NetworkErrorKind.Client.toConfirmationError(): ConfirmationError = when (errorCode) {
        ApiErrorCode.INVALID_CONFIRMATION_CODE -> ConfirmationError.InvalidCodeError
        ApiErrorCode.CONFIRMATION_CODE_EXPIRED -> ConfirmationError.CodeExpired
        ApiErrorCode.AUTH_SESSION_EXPIRED -> ConfirmationError.SessionExpired
        ApiErrorCode.CONFIRMATION_ATTEMPTS_EXCEEDED -> ConfirmationError.AttemptsExceeded
        ApiErrorCode.TOO_MANY_REQUESTS -> ConfirmationError.TooManyRequestError(retryAfterSeconds)
        else -> when (statusCode) {
            HttpStatusCodes.BAD_REQUEST -> ConfirmationError.InvalidCodeError
            HttpStatusCodes.GONE -> ConfirmationError.CodeExpired
            HttpStatusCodes.TOO_MANY_REQUESTS -> ConfirmationError.TooManyRequestError(retryAfterSeconds)
            else -> ConfirmationError.UnknownError(message)
        }
    }

    private fun Throwable.toResendConfirmationError(): ResendConfirmationError =
        when (val kind = classifyNetworkError()) {
            is NetworkErrorKind.Client -> kind.toResendConfirmationError()
            is NetworkErrorKind.Server -> ResendConfirmationError.ServerError
            is NetworkErrorKind.Network -> ResendConfirmationError.NetworkError
            is NetworkErrorKind.Unknown -> ResendConfirmationError.UnknownError(kind.message)
        }

    private fun NetworkErrorKind.Client.toResendConfirmationError(): ResendConfirmationError = when (errorCode) {
        ApiErrorCode.AUTH_SESSION_EXPIRED -> ResendConfirmationError.SessionExpired
        ApiErrorCode.TOO_MANY_REQUESTS -> ResendConfirmationError.TooManyRequestError(retryAfterSeconds)
        ApiErrorCode.VALIDATION_ERROR,
        ApiErrorCode.INVALID_FORMAT,
        ApiErrorCode.INVALID_ARGUMENT,
        -> ResendConfirmationError.InvalidPhone

        else -> when (statusCode) {
            HttpStatusCodes.GONE -> ResendConfirmationError.SessionExpired
            HttpStatusCodes.BAD_REQUEST -> ResendConfirmationError.InvalidPhone
            HttpStatusCodes.TOO_MANY_REQUESTS -> ResendConfirmationError.TooManyRequestError(retryAfterSeconds)
            else -> ResendConfirmationError.UnknownError(message)
        }
    }
}
