package com.zavgar.system.network.model

import com.zavgar.system.core.serializer.LocalDateTimeDDMMYYYYHHMMSSSerializer
import com.zavgar.system.network.model.serializer.OperationTypeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    @SerialName("id")
    val id: Int,
    @SerialName("operationType")
    @Serializable(with = OperationTypeSerializer::class)
    val operationType: OperationType,
    @SerialName("date")
    @Serializable(with = LocalDateTimeDDMMYYYYHHMMSSSerializer::class)
    val date: LocalDateTime,
    @SerialName("store")
    val store: String,
    @SerialName("amount")
    val amount: Int,
    @SerialName("pointsType")
    val pointsType: PointsType,
    @SerialName("phone")
    val phone: String
)
