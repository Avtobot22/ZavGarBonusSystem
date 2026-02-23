package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.RegisterError
import com.zavgar.system.domain.model.request.RegisterRequest
import com.zavgar.system.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(registerRequest: RegisterRequest): AppResult<Unit, RegisterError> =
        authRepository.register(registerRequest)
}