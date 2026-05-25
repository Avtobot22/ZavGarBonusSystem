package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.domain.auth.model.RegisterRequest
import com.zavgar.system.utils.result.AppResult

class RegisterUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(request: RegisterRequest): AppResult<Unit, RegisterError> =
        authRepository.register(
            name = request.name,
            birthDate = request.birthDate,
            phone = request.phone,
        )
}
