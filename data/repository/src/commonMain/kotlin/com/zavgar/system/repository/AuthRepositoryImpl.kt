package com.zavgar.system.repository

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.AuthError
import com.zavgar.system.domain.model.error.ConfirmationError
import com.zavgar.system.domain.model.error.RegisterError
import com.zavgar.system.domain.model.error.ResendConfirmationError
import com.zavgar.system.domain.model.error.ResetPasswordError
import com.zavgar.system.domain.model.request.ConfirmationRequest
import com.zavgar.system.domain.model.request.LoginRequest
import com.zavgar.system.domain.model.request.RegisterRequest
import com.zavgar.system.domain.model.request.ResendRequest
import com.zavgar.system.domain.model.request.ResetPasswordRequest
import com.zavgar.system.domain.repository.AuthRepository
import com.zavgar.system.repository.datasource.SessionDataSource
import com.zavgar.system.repository.mapper.toAuthError
import com.zavgar.system.repository.mapper.toConfirmationError
import com.zavgar.system.repository.mapper.toDomainAuth
import com.zavgar.system.repository.mapper.toDomainConfirmation
import com.zavgar.system.repository.mapper.toDomainRegister
import com.zavgar.system.repository.mapper.toDomainResendConfirmation
import com.zavgar.system.repository.mapper.toDomainResetPassword
import com.zavgar.system.repository.mapper.toRegisterError
import com.zavgar.system.repository.mapper.toRepo
import com.zavgar.system.repository.mapper.toResendConfirmationError
import com.zavgar.system.repository.mapper.toResetPasswordError
import com.zavgar.system.repository.mapper.toSession
import com.zavgar.system.repository.remote.AuthService
import com.zavgar.system.repository.util.toRepoResult
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionDataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): AppResult<Unit, AuthError> =
        withContext(dispatcherProvider.io) {
            val response = when (val result = authService.loginRequest(loginRequest.toRepo())
                .toRepoResult(Throwable::toAuthError)
                .toDomainAuth()) {
                is AppResult.Error -> return@withContext result
                is AppResult.Success -> result.data
            }

            sessionDataSource.saveSession(response.toSession(loginRequest.phone))
                .onFailure { exception ->
                    return@withContext AppResult.Error(
                        AuthError.UnknownError(exception.message ?: "Storage Error")
                    )
                }

            AppResult.Success(Unit)
        }

    override suspend fun register(registerRequest: RegisterRequest): AppResult<Unit, RegisterError> =
        withContext(dispatcherProvider.io) {
            authService.registerRequest(registerRequest.toRepo())
                .toRepoResult(Throwable::toRegisterError)
                .toDomainRegister()
        }

    override suspend fun confirmRegistration(confirmationRequest: ConfirmationRequest): AppResult<Unit, ConfirmationError> =
        withContext(dispatcherProvider.io) {
            authService.confirmRegistration(confirmationRequest.toRepo())
                .toRepoResult(Throwable::toConfirmationError)
                .toDomainConfirmation()
        }

    override suspend fun confirmReset(confirmationRequest: ConfirmationRequest): AppResult<Unit, ConfirmationError> =
        withContext(dispatcherProvider.io) {
            authService.confirmReset(confirmationRequest.toRepo())
                .toRepoResult(Throwable::toConfirmationError)
                .toDomainConfirmation()
        }

    override suspend fun resendCode(resendRequest: ResendRequest): AppResult<Unit, ResendConfirmationError> =
        withContext(dispatcherProvider.io) {
            authService.resendCode(resendRequest.toRepo())
                .toRepoResult(Throwable::toResendConfirmationError)
                .toDomainResendConfirmation()
        }

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): AppResult<Unit, ResetPasswordError> =
        withContext(dispatcherProvider.io) {
            authService.resetPassword(resetPasswordRequest.toRepo())
                .toRepoResult(Throwable::toResetPasswordError)
                .toDomainResetPassword()
        }
}
