package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.ResetPasswordError
import com.zavgar.system.domain.auth.model.ResetPasswordRequest
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class ResetPasswordUseCase(
    private val authRepository: AuthRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(request: ResetPasswordRequest): AppResult<Unit, ResetPasswordError> =
        withContext(dispatcherProvider.io) {
            authRepository.resetPassword(phone = request.phone, newPassword = request.newPassword)
        }
}
