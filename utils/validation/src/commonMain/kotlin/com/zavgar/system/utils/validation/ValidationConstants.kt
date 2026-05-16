package com.zavgar.system.utils.validation

const val PHONE_LENGTH: Int = 10
const val CODE_LENGTH: Int = 4

fun sanitizePhone(raw: String): String =
    raw.filter(Char::isDigit).take(PHONE_LENGTH)
