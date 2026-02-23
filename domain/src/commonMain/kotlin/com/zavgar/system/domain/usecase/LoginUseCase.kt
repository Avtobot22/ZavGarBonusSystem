package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.model.request.LoginRequest
import com.zavgar.system.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(loginRequest: LoginRequest) = authRepository.login(loginRequest)
}