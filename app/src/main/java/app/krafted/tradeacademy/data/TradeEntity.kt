package app.krafted.tradeacademy.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

enum class TradeType { BUY, SELL }

class TradeTypeConverter {
    @TypeConverter
    fun fromTradeType(type: TradeType): String = type.name

    @TypeConverter
    fun toTradeType(value: String): TradeType = TradeType.valueOf(value)
}

@Entity(tableName = "trades")
@TypeConverters(TradeTypeConverter::class)
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetId: String,
    val type: TradeType,
    val quantity: Double,
    val price: Double,
    val timestamp: Long = System.currentTimeMillis()
)
