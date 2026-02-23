package com.zavgar.system.network.model

import kotlinx.serialization.Serializable

@Serializable
enum class OperationType {
    CREDITING,
    DEBITING
}