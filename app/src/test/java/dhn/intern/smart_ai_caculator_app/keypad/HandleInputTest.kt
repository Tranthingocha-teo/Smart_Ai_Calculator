package dhn.intern.smart_ai_caculator_app.keypad

import dhn.intern.smart_ai_caculator_app.ui.components.keypad.handleInput
import org.junit.Assert.assertEquals
import org.junit.Test

class HandleInputTest {

    @Test
    fun testDigitInputReplacesInitialZero() {
        assertEquals("5", handleInput("0", "5"))
        assertEquals("0", handleInput("0", "0"))
        assertEquals("52", handleInput("5", "2"))
    }

    @Test
    fun testSingleDecimalPoint() {
        assertEquals("0.", handleInput("0", "."))
        assertEquals("1.5", handleInput("1.", "5"))
        // Reject second decimal point
        assertEquals("1.5", handleInput("1.5", "."))
    }

    @Test
    fun testSignToggle() {
        assertEquals("-5", handleInput("5", "+/-"))
        assertEquals("5", handleInput("-5", "+/-"))
        assertEquals("-", handleInput("0", "+/-"))
    }

    @Test
    fun testBackspace() {
        assertEquals("12", handleInput("123", "⌫"))
        assertEquals("0", handleInput("1", "⌫"))
        assertEquals("0", handleInput("-5", "⌫"))
        assertEquals("0", handleInput("0", "⌫"))
    }

    @Test
    fun testClear() {
        assertEquals("0", handleInput("12345", "C"))
    }
}
