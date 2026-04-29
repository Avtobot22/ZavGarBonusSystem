package com.zavgar.system.resetpassword.domain.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.resetpassword.domain.error.ResetPasswordError
import com.zavgar.system.resetpassword.domain.error.toResetPasswordError
import com.zavgar.system.resetpassword.model.ResetPasswordRequest
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.ResetPasswordRequest as NetworkResetPasswordRequest

class ResetPasswordUseCase(
    private val authService: AuthService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(resetPasswordRequest: ResetPasswordRequest): AppResult<Unit, ResetPasswordError> =
        withContext(dispatcherProvider.io) {
            authService.resetPassword(
                NetworkResetPasswordRequest(
                    phone = resetPasswordRequest.phone,
                    password = resetPasswordRequest.newPassword,
                )
            ).toAppResult(Throwable::toResetPasswordError)
        }
}
