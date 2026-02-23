package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.ConfirmationRequest as NetworkConfirmationRequest
import com.zavgar.system.network.model.ResendRequest as NetworkResendRequest
import com.zavgar.system.repository.model.request.ConfirmationRequest as RepoConfirmationRequest
import com.zavgar.system.repository.model.request.ResendRequest as RepoResendRequest


fun RepoConfirmationRequest.toNetwork() = NetworkConfirmationRequest(
    phone = "+7${this.phone}",
    code = this.code
)

fun RepoResendRequest.toNetwork() = NetworkResendRequest(
    phone = "+7${this.phone}"
)