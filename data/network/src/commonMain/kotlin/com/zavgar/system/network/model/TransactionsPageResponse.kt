package com.zavgar.system.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionsPageResponse(
    @SerialName("items")
    val items: List<Transaction>,
    @SerialName("nextCursor")
    val nextCursor: String? = null,
    @SerialName("hasMore")
    val hasMore: Boolean
)
