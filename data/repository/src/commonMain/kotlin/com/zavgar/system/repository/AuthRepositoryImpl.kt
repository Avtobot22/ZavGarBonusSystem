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
import kotlinx.coroutines.withContext
import com.zavgar.system.repository.model.AppResult as RepoAppResult
import com.zavgar.system.repository.model.error.AuthError as RepoAuthError

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val sessionDataSource: SessionDataSource,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): AppResult<Unit, AuthError> =
        withContext(dispatcherProvider.io) {

            // Делаем запрос в сеть
            val apiResult = authService.loginRequest(loginRequest.toRepo())

            // Обрабатываем ошибку сети
            val response = apiResult.getOrElse { exception ->
                return@withContext RepoAppResult.Error(exception.toAuthError())
                    .toDomainAuth()
            }

            // Сохранение сессии
            sessionDataSource.saveSession(response.toSession(loginRequest.phone))
                .onFailure { exception ->
                    return@withContext RepoAppResult.Error(
                        RepoAuthError.UnknownError(exception.message ?: "Storage Error")
                    ).toDomainAuth()
                }

            // Успех
            RepoAppResult.Success(Unit).toDomainAuth()
        }

    override suspend fun register(registerRequest: RegisterRequest): AppResult<Unit, RegisterError> =
        withContext(dispatcherProvider.io) {

            // Делаем запрос в сеть
            val apiResult = authService.registerRequest(registerRequest.toRepo())

            // Обрабатываем ошибку сети
            val response = apiResult.getOrElse { exception ->
                return@withContext RepoAppResult.Error(exception.toRegisterError())
                    .toDomainRegister()
            }

            // Успех
            RepoAppResult.Success(response).toDomainRegister()
        }

    override suspend fun confirmRegistration(confirmationRequest: ConfirmationRequest): AppResult<Unit, ConfirmationError> =
        withContext(dispatcherProvider.io) {

            val apiResult = authService.confirmRegistration(confirmationRequest.toRepo())

            val response = apiResult.getOrElse { exception ->
                return@withContext RepoAppResult.Error(exception.toConfirmationError())
                    .toDomainConfirmation()
            }

            RepoAppResult.Success(response).toDomainConfirmation()
        }

    override suspend fun confirmReset(confirmationRequest: ConfirmationRequest): AppResult<Unit, ConfirmationError> =
        withContext(dispatcherProvider.io) {

            val apiResult = authService.confirmReset(confirmationRequest.toRepo())

            val response = apiResult.getOrElse { exception ->
                return@withContext RepoAppResult.Error(exception.toConfirmationError())
                    .toDomainConfirmation()
            }

            RepoAppResult.Success(response).toDomainConfirmation()
        }

    override suspend fun resendCode(resendRequest: ResendRequest): AppResult<Unit, ResendConfirmationError> =
        withContext(dispatcherProvider.io) {

            val apiResult = authService.resendCode(resendRequest.toRepo())

            val response = apiResult.getOrElse { exception ->
                return@withContext RepoAppResult.Error(exception.toResendConfirmationError())
                    .toDomainResendConfirmation()
            }

            RepoAppResult.Success(response).toDomainResendConfirmation()
        }

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): AppResult<Unit, ResetPasswordError> =
        withContext(dispatcherProvider.io) {

            val apiResult = authService.resetPassword(resetPasswordRequest.toRepo())

            val response = apiResult.getOrElse { exception ->
                return@withContext RepoAppResult.Error(exception.toResetPasswordError())
                    .toDomainResetPassword()
            }

            RepoAppResult.Success(response).toDomainResetPassword()
        }
}