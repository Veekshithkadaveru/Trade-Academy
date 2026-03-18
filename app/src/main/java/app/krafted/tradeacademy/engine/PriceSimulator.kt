package app.krafted.tradeacademy.engine

import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

object PriceSimulator {
    /**
     * Runs every 30 seconds in MarketViewModel coroutine.
     * @param currentPrice Current price before tick
     * @param volatility Category-specific (e.g. 0.03 for crypto). Must be > 0.
     * @return New price after random walk step, floored at 0.01
     */
    fun simulateTick(currentPrice: Double, volatility: Double): Double {
        val v = volatility.coerceAtLeast(0.0)
        val change = currentPrice * v * nextGaussian()
        return (currentPrice + change).coerceAtLeast(0.01)
    }

    // Marsaglia polar method for standard normal sample
    private fun nextGaussian(): Double {
        var u: Double
        var v: Double
        do {
            u = Random.nextDouble() * 2.0 - 1.0
            v = Random.nextDouble() * 2.0 - 1.0
        } while (u * u + v * v >= 1.0 || (u == 0.0 && v == 0.0))
        val w = u * u + v * v
        return u * sqrt(-2.0 * ln(w) / w)
    }
}
