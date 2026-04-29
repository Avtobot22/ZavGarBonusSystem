package com.zavgar.system.network.remote

import com.zavgar.system.coroutines.runSuspendCatching
import com.zavgar.system.network.model.ChangePasswordRequest
import com.zavgar.system.network.model.ProfileRequest
import com.zavgar.system.network.model.ProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.path

class UserProfileServiceImpl(
    private val client: HttpClient,
) : UserProfileService {
    override suspend fun getProfile() = runSuspendCatching {
        val response = client.get {
            url { path("auth/profile") }
        }
        response.body<ProfileResponse>()
    }

    override suspend fun updateProfile(profileRequest: ProfileRequest) = runSuspendCatching {
        val response = client.put {
            url { path("auth/profile") }
            setBody(profileRequest)
        }
        response.body<Unit>()
    }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) = runSuspendCatching {
        val response = client.put {
            url { path("auth/password") }
            setBody(changePasswordRequest)
        }
        response.body<Unit>()
    }

    override suspend fun logout() = runSuspendCatching {
        val response = client.delete {
            url { path("auth/logout") }
        }
        response.body<Unit>()
    }

    override suspend fun delete(): Result<Unit> = runSuspendCatching {
        val response = client.delete {
            url { path("auth/delete") }
        }
        response.body<Unit>()
    }
}
