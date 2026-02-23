package com.zavgar.system.domain.repository

import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.AuthError
import com.zavgar.system.domain.model.error.ConfirmationError
import com.zavgar.system.domain.model.error.RegisterError
import com.zavgar.system.domain.model.error.ResendConfirmationError
import com.zavgar.system.domain.model.error.ResetPasswordError
import com.zavgar.system.domain.model.request.ConfirmationRequest
import com.zavgar.system.domain.model.request.LoginRequest
import com.zavgar.system.domain.model.request.RegisterRequest
import com.zavgar.system.domain.model.request.ResendRequest
import com.zavgar.system.domain.model.request.ResetPasswordRequest

interface AuthRepository {

    suspend fun login(loginRequest: LoginRequest): AppResult<Unit, AuthError>

    suspend fun register(registerRequest: RegisterRequest): AppResult<Unit, RegisterError>

    suspend fun confirmRegistration(confirmationRequest: ConfirmationRequest): AppResult<Unit, ConfirmationError>

    suspend fun confirmReset(confirmationRequest: ConfirmationRequest): AppResult<Unit, ConfirmationError>

    suspend fun resendCode(resendRequest: ResendRequest): AppResult<Unit, ResendConfirmationError>

    suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): AppResult<Unit, ResetPasswordError>

}