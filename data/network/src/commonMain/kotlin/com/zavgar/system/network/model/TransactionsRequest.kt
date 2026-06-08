package com.zavgar.system.network.model

import com.zavgar.system.core.serializer.LocalDateDDMMYYYYSerializer
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val PAGINATION_LIMIT = 20

@Serializable
data class TransactionsRequest(
    @SerialName("periodStart")
    @Serializable(with = LocalDateDDMMYYYYSerializer::class)
    val periodStart: LocalDate,
    @SerialName("periodEnd")
    @Serializable(with = LocalDateDDMMYYYYSerializer::class)
    val periodEnd: LocalDate,
    @SerialName("cursor")
    val cursor: String? = null,
    @SerialName("limit")
    val limit: Int = PAGINATION_LIMIT,
    @SerialName("sortOrder")
    val sortOrder: SortOrder = SortOrder.DESC,
)
