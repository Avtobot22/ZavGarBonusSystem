package com.zavgar.system.network.remote

import com.zavgar.system.coroutines.runSuspendCatching
import com.zavgar.system.network.model.ConfirmationRequest
import com.zavgar.system.network.model.LoginRequest
import com.zavgar.system.network.model.LoginResponse
import com.zavgar.system.network.model.RegisterRequest
import com.zavgar.system.network.model.ResendRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path

class AuthServiceImpl(
    private val client: HttpClient,
) : AuthService {
    override suspend fun loginRequest(loginRequest: LoginRequest) = runSuspendCatching {
        val response = client.post {
            url { path("auth/login") }
            setBody(loginRequest.copy(phone = withCountryCode(loginRequest.phone)))
        }
        response.body<Unit>()
    }

    override suspend fun registerRequest(registerRequest: RegisterRequest) = runSuspendCatching {
        val response = client.post {
            url { path("auth/register") }
            setBody(registerRequest.copy(phone = withCountryCode(registerRequest.phone)))
        }
        response.body<Unit>()
    }

    override suspend fun confirmLogin(confirmationRequest: ConfirmationRequest) = runSuspendCatching {
        val response = client.post {
            url { path("auth/confirm/login") }
            setBody(confirmationRequest.copy(phone = withCountryCode(confirmationRequest.phone)))
        }
        response.body<LoginResponse>()
    }

    override suspend fun confirmRegistration(confirmationRequest: ConfirmationRequest) = runSuspendCatching {
        val response = client.post {
            url { path("auth/confirm/register") }
            setBody(confirmationRequest.copy(phone = withCountryCode(confirmationRequest.phone)))
        }
        response.body<Unit>()
    }

    override suspend fun resendCode(resendRequest: ResendRequest) = runSuspendCatching {
        val response = client.post {
            url { path("auth/refresh/code") }
            setBody(resendRequest.copy(phone = withCountryCode(resendRequest.phone)))
        }
        response.body<Unit>()
    }

    private fun withCountryCode(phone: String): String =
        if (phone.startsWith("+")) phone else "+7$phone"
}
