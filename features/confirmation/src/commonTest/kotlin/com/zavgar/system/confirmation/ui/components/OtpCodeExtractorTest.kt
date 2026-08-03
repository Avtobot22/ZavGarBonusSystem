package com.zavgar.system.confirmation.ui.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class OtpCodeExtractorTest {

    @Test
    fun `extracts a standalone code from an SMS`() {
        assertEquals("1234", extractOtpCode("Код подтверждения ZavGar: 1234", codeLength = 4))
    }

    @Test
    fun `ignores a longer digit sequence and finds the exact length code`() {
        assertEquals("9876", extractOtpCode("Заказ 123456, код 9876.", codeLength = 4))
    }

    @Test
    fun `does not extract part of a longer digit sequence`() {
        assertNull(extractOtpCode("Код: 12345", codeLength = 4))
    }

    @Test
    fun `returns null when a message has no code`() {
        assertNull(extractOtpCode("Код подтверждения не найден", codeLength = 4))
    }
}
