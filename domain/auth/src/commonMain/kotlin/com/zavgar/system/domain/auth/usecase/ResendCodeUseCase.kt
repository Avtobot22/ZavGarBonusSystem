package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.domain.auth.model.ResendRequest
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class ResendCodeUseCase(
    private val authRepository: AuthRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(request: ResendRequest): AppResult<Unit, ResendConfirmationError> =
        withContext(dispatcherProvider.io) {
            authRepository.resendCode(phone = request.phone)
        }
}
