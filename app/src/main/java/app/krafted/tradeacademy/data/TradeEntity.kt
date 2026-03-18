package app.krafted.tradeacademy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetId: String,
    val type: String,       // "BUY" or "SELL"
    val quantity: Double,
    val price: Double,
    val timestamp: Long = System.currentTimeMillis()
)
