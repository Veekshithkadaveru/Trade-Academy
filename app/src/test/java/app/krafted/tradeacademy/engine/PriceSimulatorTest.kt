package app.krafted.tradeacademy.engine

import org.junit.Assert.*
import org.junit.Test

class PriceSimulatorTest {

    @Test
    fun `simulateTick returns positive price`() {
        val price = PriceSimulator.simulateTick(100.0, 0.02)
        assertTrue("Price should be positive, was $price", price > 0)
    }

    @Test
    fun `simulateTick floors at 0_01`() {
        // Even with extreme volatility, price should never go below 0.01
        var minPrice = Double.MAX_VALUE
        repeat(1000) {
            val price = PriceSimulator.simulateTick(0.02, 1.0)
            if (price < minPrice) minPrice = price
        }
        assertTrue("Price should be at least 0.01, min was $minPrice", minPrice >= 0.01)
    }

    @Test
    fun `simulateTick with zero volatility returns same price`() {
        val price = PriceSimulator.simulateTick(100.0, 0.0)
        assertEquals(100.0, price, 0.0001)
    }

    @Test
    fun `simulateTick with negative volatility treats as zero`() {
        val price = PriceSimulator.simulateTick(100.0, -0.5)
        assertEquals(100.0, price, 0.0001)
    }

    @Test
    fun `simulateTick stays in reasonable range over many ticks`() {
        var price = 100.0
        val volatility = 0.02
        repeat(1000) {
            price = PriceSimulator.simulateTick(price, volatility)
        }
        assertTrue("Price should remain positive after 1000 ticks", price > 0)
    }

    @Test
    fun `high volatility produces larger price variations`() {
        val lowVolPrices = (1..500).map { PriceSimulator.simulateTick(100.0, 0.001) }
        val highVolPrices = (1..500).map { PriceSimulator.simulateTick(100.0, 0.1) }

        val lowRange = lowVolPrices.max() - lowVolPrices.min()
        val highRange = highVolPrices.max() - highVolPrices.min()

        assertTrue(
            "High volatility range ($highRange) should be larger than low volatility range ($lowRange)",
            highRange > lowRange
        )
    }
}
