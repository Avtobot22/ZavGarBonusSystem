package com.zavgar.system.core.presentation.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

// Экстеншены для форматирования дат в UI
fun LocalDate.toDisplayString(): String {
    return "${day.toString().padStart(2, '0')}." +
        "${month.number.toString().padStart(2, '0')}." +
        "$year"
}

fun LocalDateTime.toDayMonthYearStr(): String = date.toDisplayString()

fun LocalDateTime.toHourMinuteStr(): String {
    val h = hour.toString().padStart(2, '0')
    val m = minute.toString().padStart(2, '0')
    return "$h:$m"
}
