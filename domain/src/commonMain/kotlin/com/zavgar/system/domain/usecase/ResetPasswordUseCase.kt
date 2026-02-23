package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.ResetPasswordError
import com.zavgar.system.domain.model.request.ResetPasswordRequest
import com.zavgar.system.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(resetPasswordRequest: ResetPasswordRequest): AppResult<Unit, ResetPasswordError> {
        return authRepository.resetPassword(resetPasswordRequest)
    }
}