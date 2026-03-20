package app.krafted.tradeacademy.data

import org.junit.Assert.*
import org.junit.Test

class TradeEntityTest {

    @Test
    fun `TradeType has BUY and SELL values`() {
        val values = TradeType.values()
        assertEquals(2, values.size)
        assertTrue(values.contains(TradeType.BUY))
        assertTrue(values.contains(TradeType.SELL))
    }

    @Test
    fun `TradeTypeConverter round trips correctly`() {
        val converter = TradeTypeConverter()
        assertEquals("BUY", converter.fromTradeType(TradeType.BUY))
        assertEquals("SELL", converter.fromTradeType(TradeType.SELL))
        assertEquals(TradeType.BUY, converter.toTradeType("BUY"))
        assertEquals(TradeType.SELL, converter.toTradeType("SELL"))
    }

    @Test
    fun `TradeEntity default values are correct`() {
        val trade = TradeEntity(
            assetId = "BTC",
            type = TradeType.BUY,
            quantity = 1.5,
            price = 50000.0
        )
        assertEquals(0, trade.id)
        assertEquals("BTC", trade.assetId)
        assertEquals(TradeType.BUY, trade.type)
        assertEquals(1.5, trade.quantity, 0.001)
        assertEquals(50000.0, trade.price, 0.001)
        assertTrue(trade.timestamp > 0)
    }

    @Test
    fun `WalletEntity defaults to initial balance`() {
        val wallet = WalletEntity()
        assertEquals(1, wallet.id)
        assertEquals(INITIAL_BALANCE, wallet.cashBalance, 0.001)
    }

    @Test
    fun `HoldingEntity stores correct values`() {
        val holding = HoldingEntity(
            assetId = "AAPL",
            quantity = 10.0,
            avgBuyPrice = 150.0
        )
        assertEquals("AAPL", holding.assetId)
        assertEquals(10.0, holding.quantity, 0.001)
        assertEquals(150.0, holding.avgBuyPrice, 0.001)
    }
}
