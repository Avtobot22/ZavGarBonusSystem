package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.ResendConfirmationError
import com.zavgar.system.domain.model.request.ResendRequest
import com.zavgar.system.domain.repository.AuthRepository

class ResendCodeUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(resendRequest: ResendRequest): AppResult<Unit, ResendConfirmationError> =
        authRepository.resendCode(resendRequest)
}