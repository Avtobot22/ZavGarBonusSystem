package com.zavgar.system.network.remote

import com.zavgar.system.network.mapper.toNetwork
import com.zavgar.system.network.mapper.toRepo
import com.zavgar.system.network.model.ProfileResponse
import com.zavgar.system.repository.model.request.ChangePasswordRequest
import com.zavgar.system.repository.model.request.ProfileRequest
import com.zavgar.system.repository.remote.UserProfileService
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
    override suspend fun getProfile() = runCatching {
        val response = client.get {
            url {
                path("auth/profile")
            }
        }

        response.body<ProfileResponse>().toRepo()
    }


    override suspend fun updateProfile(profileRequest: ProfileRequest) = runCatching {
        val response = client.put {
            url {
                path("auth/profile")
            }

            setBody(profileRequest.toNetwork())
        }
        response.body<Unit>()
    }

    override suspend fun changePassword(changePasswordRequest: ChangePasswordRequest) = runCatching {
        val response = client.put {
            url {
                path("auth/password")
            }

            setBody(changePasswordRequest.toNetwork())
        }

        response.body<Unit>()
    }

    override suspend fun logout() = runCatching {
        val response = client.delete {
            url {
                path("auth/logout")
            }
        }

        response.body<Unit>()
    }

    override suspend fun delete(): Result<Unit> = runCatching {
        val response = client.delete {
            url {
                path("auth/delete")
            }
        }

        response.body<Unit>()
    }
}