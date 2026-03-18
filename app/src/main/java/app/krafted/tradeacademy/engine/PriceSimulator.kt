package app.krafted.tradeacademy.engine

import kotlin.random.Random

object PriceSimulator {
    /**
     * Runs every 30 seconds in MarketViewModel coroutine.
     * @param currentPrice Current price before tick
     * @param volatility Category-specific (e.g. 0.03 for crypto)
     * @return New price after random walk step
     */
    fun simulateTick(currentPrice: Double, volatility: Double): Double {
        val change = currentPrice * volatility * nextGaussian()
        return (currentPrice + change).coerceAtLeast(0.01)
    }

    private fun nextGaussian(): Double {
        // Box-Muller transform for Gaussian distribution
        var u: Double
        var v: Double
        do {
            u = Random.nextDouble() * 2.0 - 1.0
            v = Random.nextDouble() * 2.0 - 1.0
        } while (u * u + v * v >= 1.0 || (u == 0.0 && v == 0.0))
        val w = u * u + v * v
        return u * Math.sqrt(-2.0 * Math.log(w) / w)
    }
}
