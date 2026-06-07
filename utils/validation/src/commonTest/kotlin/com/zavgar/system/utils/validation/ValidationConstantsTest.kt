package com.zavgar.system.utils.validation

import kotlin.test.Test
import kotlin.test.assertEquals

class ValidationConstantsTest {

    @Test
    fun `sanitizePhone keeps only digits`() {
        assertEquals("1234567890", sanitizePhone("+1 (234) 567-890"))
    }

    @Test
    fun `sanitizePhone truncates to phone length`() {
        assertEquals("1234567890", sanitizePhone("12345678901234"))
    }

    @Test
    fun `sanitizePhone returns empty string when there are no digits`() {
        assertEquals("", sanitizePhone("+-() abc"))
    }

    @Test
    fun `sanitizePhone keeps fewer digits than the limit untouched`() {
        assertEquals("123", sanitizePhone("1-2-3"))
    }

    @Test
    fun `phone and code length constants hold expected values`() {
        assertEquals(10, PHONE_LENGTH)
        assertEquals(4, CODE_LENGTH)
    }
}
