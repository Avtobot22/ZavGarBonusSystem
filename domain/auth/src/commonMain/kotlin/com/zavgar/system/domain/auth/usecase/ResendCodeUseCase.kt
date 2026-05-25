package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.domain.auth.model.ResendRequest
import com.zavgar.system.utils.result.AppResult

class ResendCodeUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(request: ResendRequest): AppResult<Unit, ResendConfirmationError> =
        authRepository.resendCode(phone = request.phone)
}
