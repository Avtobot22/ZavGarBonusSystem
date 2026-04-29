package com.zavgar.system.confirmation.domain.usecase

import com.zavgar.system.confirmation.domain.error.ResendConfirmationError
import com.zavgar.system.confirmation.domain.error.toResendConfirmationError
import com.zavgar.system.confirmation.domain.model.ResendRequest
import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.ResendRequest as NetworkResendRequest

class ResendCodeUseCase(
    private val authService: AuthService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(resendRequest: ResendRequest): AppResult<Unit, ResendConfirmationError> =
        withContext(dispatcherProvider.io) {
            authService.resendCode(NetworkResendRequest(phone = resendRequest.phone))
                .toAppResult(Throwable::toResendConfirmationError)
        }
}
