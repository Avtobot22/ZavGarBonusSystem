package com.zavgar.system.network.remote

import com.zavgar.system.network.model.ConfirmationRequest
import com.zavgar.system.network.model.LoginRequest
import com.zavgar.system.network.model.LoginResponse
import com.zavgar.system.network.model.RegisterRequest
import com.zavgar.system.network.model.ResendRequest
import com.zavgar.system.network.model.ResetPasswordRequest

interface AuthService {
    suspend fun loginRequest(loginRequest: LoginRequest): Result<LoginResponse>

    suspend fun registerRequest(registerRequest: RegisterRequest): Result<Unit>

    suspend fun confirmRegistration(confirmationRequest: ConfirmationRequest): Result<Unit>

    suspend fun confirmReset(confirmationRequest: ConfirmationRequest): Result<Unit>

    suspend fun resendCode(resendRequest: ResendRequest): Result<Unit>

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Result<Unit>
}
