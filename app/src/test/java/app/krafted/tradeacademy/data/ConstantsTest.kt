package app.krafted.tradeacademy.data

import org.junit.Assert.*
import org.junit.Test

class ConstantsTest {

    @Test
    fun `initial balance is positive`() {
        assertTrue("Initial balance should be positive", INITIAL_BALANCE > 0)
    }

    @Test
    fun `initial balance is 10000`() {
        assertEquals(10000.0, INITIAL_BALANCE, 0.001)
    }
}
