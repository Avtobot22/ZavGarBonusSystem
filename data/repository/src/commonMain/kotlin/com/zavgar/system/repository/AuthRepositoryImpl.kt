package com.zavgar.system.repository

import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.datastore.model.Session
import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.domain.auth.error.ResetPasswordError
import com.zavgar.system.network.mapper.NetworkErrorKind
import com.zavgar.system.network.mapper.classifyNetworkError
import com.zavgar.system.network.model.ConfirmationRequest
import com.zavgar.system.network.model.LoginRequest
import com.zavgar.system.network.model.RegisterRequest
import com.zavgar.system.network.model.ResendRequest
import com.zavgar.system.network.model.ResetPasswordRequest
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.utils.result.AppResult
import kotlinx.datetime.LocalDate

internal class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionDataSource: SessionDataSource,
) : AuthRepository {

    override suspend fun login(phone: String, password: String): AppResult<Unit, AuthError> {
        val response = authService.loginRequest(LoginRequest(phone = phone, password = password))
            .fold(
                onSuccess = { it },
                onFailure = { return AppResult.Error(it.toAuthError()) },
            )
        sessionDataSource.saveSession(
            Session(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                phone = phone,
            )
        ).onFailure { exception ->
            return AppResult.Error(AuthError.UnknownError(exception.message ?: "Storage Error"))
        }
        return AppResult.Success(Unit)
    }

    override suspend fun register(
        name: String,
        birthDate: LocalDate,
        phone: String,
        password: String,
    ): AppResult<Unit, RegisterError> =
        authService.registerRequest(
            RegisterRequest(name = name, birthDate = birthDate, phone = phone, password = password)
        ).toAppResult { it.toRegisterError() }

    override suspend fun confirmRegistration(phone: String, code: String): AppResult<Unit, ConfirmationError> =
        authService.confirmRegistration(ConfirmationRequest(phone = phone, code = code))
            .toAppResult { it.toConfirmationError() }

    override suspend fun confirmReset(phone: String, code: String): AppResult<Unit, ConfirmationError> =
        authService.confirmReset(ConfirmationRequest(phone = phone, code = code))
            .toAppResult { it.toConfirmationError() }

    override suspend fun resendCode(phone: String): AppResult<Unit, ResendConfirmationError> =
        authService.resendCode(ResendRequest(phone = phone))
            .toAppResult { it.toResendConfirmationError() }

    override suspend fun resetPassword(phone: String, newPassword: String): AppResult<Unit, ResetPasswordError> =
        authService.resetPassword(ResetPasswordRequest(phone = phone, password = newPassword))
            .toAppResult { it.toResetPasswordError() }

    private fun <T, E> Result<T>.toAppResult(errorMapper: (Throwable) -> E): AppResult<T, E> =
        fold(
            onSuccess = { AppResult.Success(it) },
            onFailure = { AppResult.Error(errorMapper(it)) },
        )

    private fun Throwable.toAuthError(): AuthError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            400 -> AuthError.ValidationError
            401 -> AuthError.UserNotFound
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

    private fun Throwable.toResetPasswordError(): ResetPasswordError = when (val kind = classifyNetworkError()) {
        is NetworkErrorKind.Client -> when (kind.statusCode) {
            400 -> ResetPasswordError.InvalidPhoneError
            404 -> ResetPasswordError.UserNotFound
            429 -> ResetPasswordError.TooManyRequestError
            else -> ResetPasswordError.UnknownError(kind.message)
        }
        is NetworkErrorKind.Server -> ResetPasswordError.ServerError
        is NetworkErrorKind.Network -> ResetPasswordError.NetworkError
        is NetworkErrorKind.Unknown -> ResetPasswordError.UnknownError(kind.message)
    }
}
