package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.domain.auth.model.LoginRequest
import com.zavgar.system.utils.result.AppResult

class LoginUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(request: LoginRequest): AppResult<Unit, AuthError> =
        authRepository.login(phone = request.phone)
}
