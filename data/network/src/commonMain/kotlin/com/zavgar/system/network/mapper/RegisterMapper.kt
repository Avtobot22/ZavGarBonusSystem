package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.RegisterRequest as NetworkRegisterRequest
import com.zavgar.system.repository.model.request.RegisterRequest as RepoRegisterRequest


fun RepoRegisterRequest.toNetwork() = NetworkRegisterRequest(
    name = this.name,
    birthDate = this.birthDate,
    phone = "+7${this.phone}",
    password = this.password
)

