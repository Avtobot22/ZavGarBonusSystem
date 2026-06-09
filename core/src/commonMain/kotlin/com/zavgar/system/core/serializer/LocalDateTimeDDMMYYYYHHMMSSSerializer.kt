package com.zavgar.system.core.serializer

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Сериализует [LocalDateTime] в НЕСТАНДАРТНОМ текстовом формате `dd.MM.yyyy HH:mm:ss`,
 * которого требует ZavGar Server API (поле `date` в транзакциях и т.п.).
 *
 * ВНИМАНИЕ: это НЕ ISO-8601 (`yyyy-MM-ddTHH:mm:ss`), хотя в OpenAPI-схеме поля помечены как
 * `format: date-time`. Контракт задаёт формат текстом в описании; при переходе сервера на ISO
 * этот сериализатор (и парный [LocalDateDDMMYYYYSerializer]) необходимо обновить синхронно.
 */
// Positional parsing of the dd.MM.yyyy HH:mm:ss layout: the part-count and indices are
// inherent to the format, not magic constants worth extracting.
@Suppress("MagicNumber")
object LocalDateTimeDDMMYYYYHHMMSSSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val day = value.day.toString().padStart(2, '0')
        val month = value.month.number.toString().padStart(2, '0')
        val year = value.year.toString()
        val hour = value.hour.toString().padStart(2, '0')
        val minute = value.minute.toString().padStart(2, '0')
        val second = value.second.toString().padStart(2, '0')

        val formatted = "$day.$month.$year $hour:$minute:$second"

        encoder.encodeString(formatted)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val dateString = decoder.decodeString()
        val parts = dateString.split(".", ":", " ")
        require(parts.size == 6) { "Invalid date format: $dateString. Expected: dd.MM.yyyy HH:mm:ss" }

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        val hour = parts[3].toInt()
        val minute = parts[4].toInt()
        val second = parts[5].toInt()

        return LocalDateTime(year, month, day, hour, minute, second)
    }
}
