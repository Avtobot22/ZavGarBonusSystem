package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.ResetPasswordRequest as NetworkResetPasswordRequest
import com.zavgar.system.repository.model.request.ResetPasswordRequest as RepoResetPasswordRequest


fun RepoResetPasswordRequest.toNetwork() = NetworkResetPasswordRequest(
    phone = "+7${this.phone}",
    password = password
)
