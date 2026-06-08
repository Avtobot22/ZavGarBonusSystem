package com.zavgar.system.core.serializer

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// Positional parsing of the dd.MM.yyyy layout: the part-count and indices are inherent to
// the format, not magic constants worth extracting.
@Suppress("MagicNumber")
object LocalDateDDMMYYYYSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        val formatted = "${value.day.toString().padStart(2, '0')}." +
            "${value.month.number.toString().padStart(2, '0')}." +
            "${value.year}"
        encoder.encodeString(formatted)
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val dateString = decoder.decodeString()
        val parts = dateString.split(".")
        require(parts.size == 3) { "Invalid date format: $dateString. Expected: dd.MM.yyyy" }

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        return LocalDate(year, month, day)
    }
}
