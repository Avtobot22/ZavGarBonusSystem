package com.zavgar.system.confirmation.ui.components

internal fun extractOtpCode(message: String, codeLength: Int): String? {
    if (codeLength <= 0) return null

    var sequenceStart = -1
    for (index in 0..message.length) {
        val isDigit = index < message.length && message[index].isDigit()
        if (isDigit && sequenceStart < 0) {
            sequenceStart = index
        } else if (!isDigit && sequenceStart >= 0) {
            if (index - sequenceStart == codeLength) {
                return message.substring(sequenceStart, index)
            }
            sequenceStart = -1
        }
    }
    return null
}
