package com.zavgar.system.core.serializer

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Сериализует [LocalDate] в НЕСТАНДАРТНОМ текстовом формате `dd.MM.yyyy`, которого требует
 * ZavGar Server API (поля `birthDate`, `periodStart`, `periodEnd` и т.п.).
 *
 * ВНИМАНИЕ: это НЕ ISO-8601 (`yyyy-MM-dd`), хотя в OpenAPI-схеме поля помечены как `format: date`.
 * Контракт задаёт формат текстом в описании полей; при переходе сервера на ISO этот сериализатор
 * (и парный [LocalDateTimeDDMMYYYYHHMMSSSerializer]) необходимо обновить синхронно.
 */
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
