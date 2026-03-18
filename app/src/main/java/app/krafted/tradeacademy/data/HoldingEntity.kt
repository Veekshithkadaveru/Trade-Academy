package app.krafted.tradeacademy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey val assetId: String,
    val quantity: Double,
    val avgBuyPrice: Double
)
