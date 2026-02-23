package com.zavgar.system.repository.remote

import com.zavgar.system.repository.model.request.ConfirmationRequest
import com.zavgar.system.repository.model.request.LoginRequest
import com.zavgar.system.repository.model.request.RegisterRequest
import com.zavgar.system.repository.model.request.ResendRequest
import com.zavgar.system.repository.model.request.ResetPasswordRequest
import com.zavgar.system.repository.model.response.LoginResponse

interface AuthService {
    suspend fun loginRequest(loginRequest: LoginRequest): Result<LoginResponse>

    suspend fun registerRequest(registerRequest: RegisterRequest): Result<Unit>

    suspend fun confirmRegistration(confirmationRequest: ConfirmationRequest): Result<Unit>

    suspend fun confirmReset(confirmationRequest: ConfirmationRequest): Result<Unit>

    suspend fun resendCode(resendRequest: ResendRequest): Result<Unit>

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Result<Unit>
}