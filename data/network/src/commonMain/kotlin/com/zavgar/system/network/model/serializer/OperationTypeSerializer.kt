package com.zavgar.system.network.model.serializer

import com.zavgar.system.network.model.OperationType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object OperationTypeSerializer : KSerializer<OperationType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("OperationType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OperationType) {
        val result = when (value) {
            OperationType.CREDITING -> "+"
            OperationType.DEBITING -> "-"
        }
        encoder.encodeString(result)
    }

    override fun deserialize(decoder: Decoder): OperationType {
        return when (decoder.decodeString()) {
            "+" -> OperationType.CREDITING
            "-" -> OperationType.DEBITING
            else -> throw IllegalArgumentException("Invalid OperationType")
        }
    }
}
