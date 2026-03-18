package app.krafted.tradeacademy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeDao {
    @Query("SELECT * FROM trades ORDER BY timestamp DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Insert
    suspend fun insertTrade(trade: TradeEntity)

    @Query("DELETE FROM trades")
    suspend fun clearAll()
}
