package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.domain.auth.model.LoginRequest
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(request: LoginRequest): AppResult<Unit, AuthError> =
        withContext(dispatcherProvider.io) {
            authRepository.login(phone = request.phone)
        }
}
