package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.BalanceResponse as NetworkBalanceResponse
import com.zavgar.system.repository.model.response.BalanceResponse as RepoBalanceResponse

fun NetworkBalanceResponse.toRepo() = RepoBalanceResponse(
    balance = balance,
)