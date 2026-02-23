package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.ChangePasswordRequest as NetworkChangePasswordRequest
import com.zavgar.system.repository.model.request.ChangePasswordRequest as RepoChangePasswordRequest

fun RepoChangePasswordRequest.toNetwork() = NetworkChangePasswordRequest(
    oldPassword = oldPassword,
    newPassword = newPassword
)